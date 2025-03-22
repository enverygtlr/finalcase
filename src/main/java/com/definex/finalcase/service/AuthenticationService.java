package com.definex.finalcase.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.definex.finalcase.domain.request.LoginRequest;
import com.definex.finalcase.domain.response.LoginResponse;
import com.definex.finalcase.security.JwtService;
import com.definex.finalcase.security.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    public LoginResponse authenticate(LoginRequest request, HttpServletResponse response) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var principal = (UserPrincipal) authentication.getPrincipal();
        var accessToken = jwtService.issue(principal);
        var refreshToken = jwtService.issueRefreshToken(principal);

        Cookie cookie = new Cookie("r_t_", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);

        return getLoginResponse(principal, accessToken);
    }

    public void logout(HttpServletResponse response) {
        SecurityContextHolder.getContext().setAuthentication(null);

        Cookie cookie = new Cookie("r_t_", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

    public LoginResponse refresh(HttpServletRequest request) {
        var refreshToken = Arrays.stream(request.getCookies()).filter(cookie -> Objects.equals(cookie.getName(), "r_t_")).findFirst().orElse(null);
        if (refreshToken == null) {
            throw new JWTVerificationException("Refresh token not found");
        }

        var principal = jwtService.verifyToken(refreshToken.getValue());
        principal = userService.getUserByEmail(principal.getEmail());

        var accessToken = jwtService.issue(principal);

        return getLoginResponse(principal, accessToken);
    }

    private LoginResponse getLoginResponse(UserPrincipal principal, String accessToken) {
        var user = userService.getUserById(principal.getUserId());
        return LoginResponse.builder()
                .token(accessToken)
                .user(user)
                .build();
    }
}
