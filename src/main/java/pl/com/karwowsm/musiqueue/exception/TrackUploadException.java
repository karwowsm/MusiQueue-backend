package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TrackUploadException extends RuntimeException {

    public TrackUploadException(Throwable cause) {
        super("Something went wrong during uploading track", cause);
    }
}
