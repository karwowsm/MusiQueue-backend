package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserQueuedTracksLimitExceededException extends RuntimeException {

    public UserQueuedTracksLimitExceededException(Integer userQueuedTracksLimit) {
        super(String.format("Limit(%s) for tracks queued by user cannot be exceeded", userQueuedTracksLimit));
    }
}
