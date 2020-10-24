package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.karwowsm.musiqueue.api.request.UserAccountCreateRequest;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.service.UserAccountService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService service;

    @PostMapping
    public UserAccount register(@RequestBody @Valid UserAccountCreateRequest request) {
        log.trace("Creating userAccount: request={}", request);
        UserAccount userAccount = service.create(request);
        log.debug("Created userAccount: {}", userAccount);
        return userAccount;
    }

    @GetMapping("/me")
    public UserAccount getMe(@AuthenticationPrincipal UserAccount userAccount) {
        log.debug("Got me: {}", userAccount);
        return userAccount;
    }
}
