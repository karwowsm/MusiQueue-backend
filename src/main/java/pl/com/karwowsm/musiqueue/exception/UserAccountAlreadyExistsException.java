package pl.com.karwowsm.musiqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserAccountAlreadyExistsException extends RuntimeException {

    private UserAccountAlreadyExistsException(String param) {
        super(String.format("User with given %s already exists", param));
    }

    public static UserAccountAlreadyExistsException ofUsername() {
        return new UserAccountAlreadyExistsException("username");
    }

    public static UserAccountAlreadyExistsException ofEmail() {
        return new UserAccountAlreadyExistsException("email");
    }
}
