package pl.com.karwowsm.musiqueue.service;

import pl.com.karwowsm.musiqueue.api.dto.RoomTracklist;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

import java.util.UUID;

public interface RoomTracklistService {

    RoomTracklist get(UUID roomId);

    RoomTrack addTrack(UUID roomId, Track track, UserAccount userAccount);

    RoomTrack deleteTrack(UUID roomId, UUID roomTrackId, UserAccount userAccount);

    RoomTrack playNext(UUID roomId);

    RoomTrack playTrack(UUID roomId, UUID roomTrackId, UserAccount userAccount);
}
