package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class resourceType, UUID id) {
        super(String.format("%s(id=%s) not found", resourceType.getSimpleName(), id));
    }

    public ResourceNotFoundException(Class resourceType, String params) {
        super(String.format("%s with given params(%s) not found", resourceType.getSimpleName(), params));
    }
}
