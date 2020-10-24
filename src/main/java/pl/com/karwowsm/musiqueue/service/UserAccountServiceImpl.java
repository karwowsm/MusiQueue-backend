package pl.com.karwowsm.musiqueue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.karwowsm.musiqueue.api.request.UserAccountCreateRequest;
import pl.com.karwowsm.musiqueue.exception.UserAccountAlreadyExistsException;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.UserAccountRepository;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final PasswordEncoder passwordEncoder;

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount create(UserAccountCreateRequest request) {
        userAccountRepository.findByUsername(request.getUsername())
            .ifPresent(userAccount -> {
                throw UserAccountAlreadyExistsException.ofUsername();
            });
        userAccountRepository.findByEmail(request.getEmail())
            .ifPresent(userAccount -> {
                throw UserAccountAlreadyExistsException.ofEmail();
            });

        UserAccount userAccount = userAccountRepository.saveAndFlush(UserAccount.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .build());

        return userAccount;
    }
}
