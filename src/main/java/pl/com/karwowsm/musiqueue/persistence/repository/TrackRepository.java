package pl.com.karwowsm.musiqueue.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {

    Track findBySourceAndTrackId(Track.Source source, String trackId);
}
