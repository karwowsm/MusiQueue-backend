package pl.com.karwowsm.musiqueue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.com.karwowsm.musiqueue.api.request.RoomCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.RoomUpdateRequest;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.exception.ResourceOwnershipException;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomRepository;
import pl.com.karwowsm.musiqueue.ws.MessagingService;
import pl.com.karwowsm.musiqueue.ws.dto.RoomEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final MessagingService messagingService;

    private final RoomRepository repository;

    @Override
    public Room create(UserAccount userAccount, RoomCreateRequest request) {
        Room room = Room.builder()
            .name(request.getName())
            .userQueuedTracksLimit(request.getUserQueuedTracksLimit())
            .host(userAccount)
            .build();

        return repository.saveAndFlush(room);
    }

    @Override
    public Page<Room> find(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Room get(UUID id) {
        return repository.get(id);
    }

    @Override
    public void assertExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(Room.class, id);
        }
    }

    @Override
    public Room update(UUID id, RoomUpdateRequest request, UserAccount userAccount) {
        Room room = get(id);
        checkOwnership(room, userAccount);

        room.setName(request.getName());
        room.setUserQueuedTracksLimit(request.getUserQueuedTracksLimit());
        room = repository.saveAndFlush(room);

        messagingService.publishRoomEvent(room, RoomEvent.Type.UPDATED);
        return room;
    }

    @Override
    public void delete(UUID id, UserAccount userAccount) {
        Room room = get(id);
        checkOwnership(room, userAccount);

        repository.delete(room);
        messagingService.publishRoomEvent(room, RoomEvent.Type.DELETED);
    }

    private void checkOwnership(Room room, UserAccount userAccount) {
        if (!room.getHost().getId().equals(userAccount.getId())) {
            throw new ResourceOwnershipException(room.getClass(), room.getId());
        }
    }
}
