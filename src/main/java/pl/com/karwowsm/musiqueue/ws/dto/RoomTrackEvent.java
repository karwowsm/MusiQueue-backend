package pl.com.karwowsm.musiqueue.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;

import java.time.Instant;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RoomTrackEvent extends Event<RoomTrackEvent.Type> {

    private final Type type;

    private final RoomTrack track;

    private Instant timestamp = Instant.now();

    public enum Type {
        ADDED,
        DELETED,
        PLAYED
    }
}
