package pl.com.karwowsm.musiqueue.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class RoomTracklist {

    private final List<RoomTrack> tracklist;

    private final UUID currentTrackId;

    private final Instant startedPlayingAt;

    public static RoomTracklist of(List<RoomTrack> tracklist, Room room) {
        return new RoomTracklist(tracklist,
                Optional.ofNullable(room.getCurrentTrack()).map(RoomTrack::getId).orElse(null),
                room.getStartedPlayingAt()
        );
    }
}
