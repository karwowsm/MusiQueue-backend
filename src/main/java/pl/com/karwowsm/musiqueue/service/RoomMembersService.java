package pl.com.karwowsm.musiqueue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

public interface RoomMembersService {

    Page<UserAccount> findUserAccount(Room room, Pageable pageable);

    Room join(Room room, UserAccount userAccount);

    Room leave(Room room, UserAccount userAccount);
}
