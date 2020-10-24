package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RoomTrackAddException extends RuntimeException {

    public RoomTrackAddException(Throwable cause) {
        super("Something went wrong during adding track", cause);
    }
}
