package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.Track;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RoomTrackPlayerException extends RuntimeException {

    public RoomTrackPlayerException(Room room) {
        super(String.format("Nothing is playing in %s", room));
    }

    public RoomTrackPlayerException(Room room, UUID roomTrackId) {
        super(String.format("%s is not playing in %s", roomTrackId, room));
    }

    public RoomTrackPlayerException(Track track) {
        super(String.format("%s is not local file", track));
    }
}
