package pl.com.karwowsm.musiqueue.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.karwowsm.musiqueue.persistence.model.RoomMember;

import java.util.UUID;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {

    RoomMember findByUserAccountIdAndRoomId(UUID userAccountId, UUID roomId);

    Page<RoomMember> findAllByRoomId(UUID roomId, Pageable pageable);
}
