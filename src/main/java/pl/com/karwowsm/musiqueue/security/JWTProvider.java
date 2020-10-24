package pl.com.karwowsm.musiqueue.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static pl.com.karwowsm.musiqueue.security.SecurityConstants.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class JWTProvider {

    @Value("${app.security.jwt-secret}")
    private final String secret;

    @Value("${app.security.jwt-expiration-time}")
    private final long expirationTime;

    public Token generateToken(String subject) {
        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return new Token(token);
    }

    String getSubjectFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public class Token {

        private final String access_token;

        private final String token_type = TOKEN_TYPE;

        private final long expires_in = expirationTime;
    }
}
