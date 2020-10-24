package pl.com.karwowsm.musiqueue.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.persistence.model.Room;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    default Room get(UUID id) {
        return findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Room.class, id));
    }
}
