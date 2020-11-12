package pl.com.karwowsm.musiqueue.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomTrackRepository extends JpaRepository<RoomTrack, UUID> {

    List<RoomTrack> findAllByRoomIdAndIndexBetween(UUID roomId, Integer startIndex, Integer endIndex);

    List<RoomTrack> findAllByRoomIdAndIndexGreaterThanOrderByIndex(UUID roomId, Integer index);

    Integer countByRoomIdAndOwner(UUID roomId, UserAccount owner);

    default RoomTrack get(UUID id) {
        return findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RoomTrack.class, id));
    }
}
