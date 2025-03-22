package com.definex.finalcase.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.definex.finalcase.config.JwtProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String issue(UserPrincipal user) {
        var role = user.getAuthorities().stream()
                .map(Object::toString)
                .toList();
        var expires = Instant.now().plus(Duration.of(1, ChronoUnit.HOURS));
        return getJwtToken(user, expires, role);
    }

    public String issueRefreshToken(UserPrincipal user) {
        var role = List.of("REFRESH");
        var expires = Instant.now().plus(Duration.of(1, ChronoUnit.DAYS));

        return getJwtToken(user, expires, role);
    }

    public UserPrincipal verifyToken(String token) {
        return convert(decode(token));
    }

    private DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(jwtProperties.secretKey()))
                .build()
                .verify(token);
    }

    private UserPrincipal convert(DecodedJWT jwt) {
        var authorities = getAuthorities(jwt).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return UserPrincipal.builder()
                .userId(UUID.fromString(jwt.getSubject()))
                .email(jwt.getClaim("email").asString())
                .authorities(authorities)
                .build();
    }

    private String getJwtToken(UserPrincipal user, Instant expires, List<String> role) {
        return JWT.create()
                .withSubject(user.getUserId().toString())
                .withExpiresAt(expires)
                .withClaim("email", user.getEmail())
                .withClaim("role", role)
                .sign(Algorithm.HMAC256(jwtProperties.secretKey()));
    }

    private List<String> getAuthorities(DecodedJWT jwt) {
        var claim = jwt.getClaim("role");
        if (claim.isNull() || claim.isMissing()) return List.of();
        return claim.asList(String.class);
    }
}
