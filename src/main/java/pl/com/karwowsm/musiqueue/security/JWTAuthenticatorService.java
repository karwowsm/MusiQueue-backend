package pl.com.karwowsm.musiqueue.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.com.karwowsm.musiqueue.exception.ResourceNotFoundException;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class JWTAuthenticatorService {

    private final JWTProvider jwtProvider;

    private final UserAccountAuthenticationService userAccountAuthenticationService;

    AbstractAuthenticationToken getAuthenticationFromHeader(String header) {
        String token = getTokenFromHeader(header);
        if (token == null) {
            log.trace("No token provided");
            throw new AuthenticationCredentialsNotFoundException("No token provided");
        } else {
            try {
                return getAuthenticationFromToken(token);
            } catch (ExpiredJwtException e) {
                log.debug("Couldn't get authentication due to exception: {}", e.getMessage());
                throw new CredentialsExpiredException("Provided token has expired");
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("Couldn't get authentication due to exception: {}", e.getMessage());
                throw new BadCredentialsException("Invalid token provided");
            } catch (ResourceNotFoundException e) {
                log.debug("Couldn't get authentication due to exception: {}", e.getMessage());
                throw new BadCredentialsException("Invalid token provided: " + e.getMessage());
            }
        }
    }

    private String getTokenFromHeader(String header) {
        return Optional.ofNullable(header)
            .filter(StringUtils::hasText)
            .filter(h -> h.startsWith(String.format("%s ", SecurityConstants.TOKEN_TYPE)))
            .map(h -> h.substring(SecurityConstants.TOKEN_TYPE.length()).trim())
            .filter(StringUtils::hasText)
            .orElse(null);
    }

    private AbstractAuthenticationToken getAuthenticationFromToken(String token) {
        String userId = jwtProvider.getSubjectFromToken(token);
        UserAccount userAccount = userAccountAuthenticationService.loadById(UUID.fromString(userId));
        return new UsernamePasswordAuthenticationToken(userAccount, null, Collections.emptySet());
    }
}
