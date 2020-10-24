package pl.com.karwowsm.musiqueue.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.UserAccountRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountAuthenticationService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.trace("Loading user: username/email={}", usernameOrEmail);
        UserAccount userAccount = userAccountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                            log.debug("User not found: username/email={}", usernameOrEmail);
                            return new UsernameNotFoundException(usernameOrEmail);
                        }
                );
        log.debug("Loaded user: {}", userAccount);

        return new UserAccountIdentity(userAccount.getId(), userAccount.getUsername(), userAccount.getPassword());
    }

    public UserAccount loadById(UUID id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserAccount.class, id));
    }
}
