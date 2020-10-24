package pl.com.karwowsm.musiqueue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.com.karwowsm.musiqueue.api.request.RoomTrackCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.TrackUploadRequest;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import java.util.UUID;

public interface TrackService {

    Track findOrCreate(RoomTrackCreateRequest request);

    Track upload(TrackUploadRequest request);

    Page<Track> find(Pageable pageable);

    Track get(UUID id);
}
