package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceOwnershipException extends RuntimeException {

    public ResourceOwnershipException(Class resourceType, UUID id) {
        super(String.format("You are not an owner of %s(id=%s)", resourceType.getSimpleName(), id));
    }
}
