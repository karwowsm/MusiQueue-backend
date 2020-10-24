package pl.com.karwowsm.musiqueue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomMember;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomMemberRepository;
import pl.com.karwowsm.musiqueue.ws.MessagingService;
import pl.com.karwowsm.musiqueue.ws.dto.RoomMemberEvent;

@Service
@RequiredArgsConstructor
public class RoomMembersServiceImpl implements RoomMembersService {

    private final MessagingService messagingService;

    private final RoomMemberRepository roomMemberRepository;

    @Override
    public Page<UserAccount> findUserAccount(Room room, Pageable pageable) {
        Page<RoomMember> roomMembers = roomMemberRepository.findAllByRoomId(room.getId(), pageable);

        return roomMembers.map(RoomMember::getUserAccount);
    }

    @Override
    @Transactional
    public Room join(Room room, UserAccount userAccount) {
        RoomMember roomMember = roomMemberRepository.findById(userAccount.getId()).orElse(null);
        if (roomMember != null) {
            if (!roomMember.getRoomId().equals(room.getId())) {
                roomMember.setRoomId(room.getId());
                roomMember = roomMemberRepository.saveAndFlush(roomMember);
            }
        } else {
            roomMember = roomMemberRepository.saveAndFlush(RoomMember.builder()
                .userAccountId(userAccount.getId())
                .userAccount(userAccount)
                .roomId(room.getId())
                .build());
        }

        messagingService.publishRoomMemberEvent(roomMember, RoomMemberEvent.Type.JOINED);
        return room;
    }

    @Override
    public Room leave(Room room, UserAccount userAccount) {
        RoomMember roomMember = roomMemberRepository.findByUserAccountIdAndRoomId(userAccount.getId(), room.getId());
        if (roomMember == null) {
            throw new ResourceNotFoundException(RoomMember.class, String.format("userAccountId=%s, roomId=%s", userAccount.getId(), room.getId()));
        }
        roomMemberRepository.delete(roomMember);

        messagingService.publishRoomMemberEvent(roomMember, RoomMemberEvent.Type.LEFT);
        return room;
    }
}
