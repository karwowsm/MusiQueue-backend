package pl.com.karwowsm.musiqueue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.karwowsm.musiqueue.api.dto.RoomTracklist;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.exception.ResourceOwnershipException;
import pl.com.karwowsm.musiqueue.exception.RoomTrackAddException;
import pl.com.karwowsm.musiqueue.exception.RoomTrackAlreadyPlayedException;
import pl.com.karwowsm.musiqueue.exception.UserQueuedTracksLimitExceededException;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomTrackRepository;
import pl.com.karwowsm.musiqueue.ws.MessagingService;
import pl.com.karwowsm.musiqueue.ws.dto.RoomTrackEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTracklistServiceImpl implements RoomTracklistService {

    private final MessagingService messagingService;

    private final RoomRepository roomRepository;

    private final RoomTrackRepository roomTrackRepository;

    @Override
    public RoomTracklist get(UUID roomId, int offset) {
        Room room = roomRepository.get(roomId);
        Integer index = (room.getCurrentTrack() != null ? room.getCurrentTrack().getIndex() : 0) + offset - 1;
        return RoomTracklist.of(roomTrackRepository.findAllByRoomIdAndIndexGreaterThanOrderByIndex(room.getId(), index), room);
    }

    @Override
    @Transactional
    @Retryable(value = DataIntegrityViolationException.class)
    public synchronized RoomTrack addTrack(UUID roomId, Track track, UserAccount userAccount) {
        log.trace("Adding track: [roomId={}, {}, {}]", roomId, track, userAccount);
        Room room = roomRepository.get(roomId);

        RoomTrack currentTrack = room.getCurrentTrack();
        List<RoomTrack> queue = getQueue(room.getId(), currentTrack);
        long userQueuedTracks = queue.stream().filter(queuedTrack -> queuedTrack.getOwner().equals(userAccount)).count();
        if (room.getUserQueuedTracksLimit() != null && userQueuedTracks >= room.getUserQueuedTracksLimit()) {
            throw new UserQueuedTracksLimitExceededException(room.getUserQueuedTracksLimit());
        }

        RoomTrack roomTrack = queueTrack(currentTrack, queue, RoomTrack.builder()
            .roomId(room.getId())
            .track(track)
            .owner(userAccount)
            .build());

        messagingService.publishRoomTrackEvent(roomTrack, RoomTrackEvent.Type.ADDED);
        playNextIfNeeded(room);
        log.debug("Added track: {}", roomTrack);
        return roomTrack;
    }

    @Recover
    private RoomTrack addingTrackFailed(DataIntegrityViolationException e, UUID roomId, Track track, UserAccount userAccount) {
        throw new RoomTrackAddException(e);
    }

    @Recover
    private RoomTrack addingTrackFailed(Throwable e, UUID roomId, Track track, UserAccount userAccount) throws Throwable {
        throw e;
    }

    @Override
    @Transactional
    public synchronized RoomTrack deleteTrack(UUID roomId, UUID roomTrackId, UserAccount userAccount) {
        Room room = roomRepository.get(roomId);
        RoomTrack roomTrack = roomTrackRepository.get(roomTrackId);

        checkRoomIntegrity(roomTrack, room);
        checkOwnership(roomTrack, userAccount);
        if (room.getCurrentTrack().getIndex() >= roomTrack.getIndex()) {
            throw new RoomTrackAlreadyPlayedException(roomTrack);
        }
        roomTrackRepository.delete(roomTrack);

        List<RoomTrack> overtaking = roomTrackRepository.findAllByRoomIdAndIndexGreaterThanOrderByIndex(room.getId(), roomTrack.getIndex());
        overtaking.forEach(x -> x.setIndex(x.getIndex() - 1));
        roomTrackRepository.saveAll(overtaking);
        messagingService.publishRoomTrackEvent(roomTrack, RoomTrackEvent.Type.DELETED);

        return roomTrack;
    }

    @Override
    @Transactional
    public synchronized RoomTrack playNext(UUID roomId) {
        return playNextIfNeeded(roomRepository.get(roomId));
    }

    @Override
    @Transactional
    public synchronized RoomTrack playTrack(UUID roomId, UUID roomTrackId, UserAccount userAccount) {
        Room room = roomRepository.get(roomId);
        RoomTrack roomTrack = roomTrackRepository.get(roomTrackId);

        checkRoomIntegrity(roomTrack, room);
        RoomTrack currentTrack = room.getCurrentTrack();
        if (currentTrack != null) {
            if (room.isPlaying()) {
                checkOwnership(currentTrack, userAccount);
            }
            if (roomTrack.getIndex() <= currentTrack.getIndex()) {
                throw new RoomTrackAlreadyPlayedException(roomTrack);
            }
            roomTrackRepository.findAllByRoomIdAndIndexBetween(room.getId(), currentTrack.getIndex() + 1, roomTrack.getIndex() - 1)
                .forEach(x -> checkOwnership(x, userAccount));
        }

        play(room, roomTrack);
        return roomTrack;
    }

    private List<RoomTrack> getQueue(UUID roomId, RoomTrack currentTrack) {
        Integer currentTrackIndex = currentTrack != null ? currentTrack.getIndex() : -1;
        return roomTrackRepository.findAllByRoomIdAndIndexGreaterThanOrderByIndex(roomId, currentTrackIndex);
    }

    private RoomTrack queueTrack(RoomTrack currentTrack, List<RoomTrack> queue, RoomTrack roomTrack) {
        Integer userIndex = roomTrackRepository.countByRoomIdAndOwner(roomTrack.getRoomId(), roomTrack.getOwner());

        Instant now = Instant.now();
        if (queue.isEmpty()) {
            if (currentTrack == null) {
                log.trace("Queuing track: [currentTrack=null, queue.isEmpty]");
                roomTrack.setIndex(0);
            } else {
                log.trace("Queuing track: [currentTrack=[id={}, index={}], queue.isEmpty]", currentTrack.getId(), currentTrack.getIndex());
                roomTrack.setIndex(currentTrack.getIndex() + 1);
            }
        } else {
            log.trace("Queuing track: [currentTrack=[id={}, index={}], queue.size={}]", currentTrack.getId(), currentTrack.getIndex(), queue.size());
            Integer index = queue.stream()
                .filter(x -> x.getUserIndex() > userIndex
                    || Objects.equals(x.getUserIndex(), userIndex) && x.getAddedAt().isAfter(now)
                )
                .findFirst()
                .map(RoomTrack::getIndex)
                .orElse(queue.get(queue.size() - 1).getIndex() + 1);
            List<RoomTrack> overtaken = queue.stream()
                .filter(x -> x.getIndex() >= index)
                .collect(Collectors.toList());
            Collections.reverse(overtaken);
            overtaken.forEach(x -> {
                x.setIndex(x.getIndex() + 1);
                roomTrackRepository.saveAndFlush(x);
            });
            roomTrack.setIndex(index);
        }

        roomTrack.setUserIndex(userIndex);
        roomTrack.setAddedAt(now);
        log.debug("Queued track: [userIndex={}, index={}]", roomTrack.getUserIndex(), roomTrack.getIndex());
        return roomTrackRepository.saveAndFlush(roomTrack);
    }

    private RoomTrack playNextIfNeeded(Room room) {
        if (room.isPlaying()) {
            return null;
        }

        RoomTrack firstInQueue = getFirstInQueue(room.getId(), room.getCurrentTrack());
        if (firstInQueue != null) {
            play(room, firstInQueue);
        }
        return firstInQueue;
    }

    private void checkRoomIntegrity(RoomTrack roomTrack, Room room) {
        if (!roomTrack.getRoomId().equals(room.getId())) {
            throw new ResourceNotFoundException(RoomTrack.class, String.format("id=%s, roomId=%s", roomTrack.getId(), room.getId()));
        }
    }

    private void checkOwnership(RoomTrack roomTrack, UserAccount userAccount) {
        if (!roomTrack.getOwner().getId().equals(userAccount.getId())) {
            throw new ResourceOwnershipException(roomTrack.getClass(), roomTrack.getId());
        }
    }

    private void play(Room room, RoomTrack roomTrack) {
        Instant now = Instant.now();
        room.setCurrentTrack(roomTrack);
        room.setStartedPlayingAt(now);
        roomRepository.saveAndFlush(room);
        messagingService.publishRoomTrackEvent(roomTrack, RoomTrackEvent.Type.PLAYED, now);
    }

    private RoomTrack getFirstInQueue(UUID roomId, RoomTrack currentTrack) {
        return getQueue(roomId, currentTrack).stream()
            .findFirst()
            .orElse(null);
    }
}
