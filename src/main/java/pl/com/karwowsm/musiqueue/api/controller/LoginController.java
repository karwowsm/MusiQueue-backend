package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.com.karwowsm.musiqueue.api.request.TokenCreateRequest;
import pl.com.karwowsm.musiqueue.security.JWTProvider;
import pl.com.karwowsm.musiqueue.security.UserAccountIdentity;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JWTProvider jwtProvider;

    @PostMapping("/login")
    @ResponseBody
    public JWTProvider.Token login(@RequestBody @Valid TokenCreateRequest request) throws AuthenticationException {
        log.trace("Authentication attempt: {}", request);
        Authentication auth = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        UserAccountIdentity userAccountIdentity = (UserAccountIdentity) authenticationManager.authenticate(auth).getPrincipal();
        JWTProvider.Token token = jwtProvider.generateToken(userAccountIdentity.getId().toString());
        log.debug("Authentication success: token={}", token.getAccess_token());
        return token;
    }
}
