package pl.com.karwowsm.musiqueue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.com.karwowsm.musiqueue.api.request.RoomCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.RoomUpdateRequest;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

import java.util.UUID;

public interface RoomService {

    Room create(UserAccount userAccount, RoomCreateRequest request);

    Page<Room> find(Pageable pageable);

    Room get(UUID id);

    void assertExists(UUID id);

    Room update(UUID id, RoomUpdateRequest request, UserAccount userAccount);

    void delete(UUID id, UserAccount userAccount);
}
