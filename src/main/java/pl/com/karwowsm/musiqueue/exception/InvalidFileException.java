package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(Throwable cause) {
        super("Something is wrong with your file", cause);
    }
}
