package pl.com.karwowsm.musiqueue.security;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.UUID;

@Getter
public class UserAccountIdentity extends User {

    private final UUID id;

    UserAccountIdentity(UUID id, String username, String password) {
        super(username, password, Collections.emptySet());
        this.id = id;
    }
}
