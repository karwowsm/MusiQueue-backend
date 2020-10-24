package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.com.karwowsm.musiqueue.persistence.model.RoomTrack;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RoomTrackAlreadyPlayedException extends RuntimeException {

    public RoomTrackAlreadyPlayedException(RoomTrack roomTrack) {
        super(String.format("%s has already been played", roomTrack));
    }
}
