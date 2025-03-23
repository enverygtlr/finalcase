package com.definex.finalcase.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.definex.finalcase.domain.request.LoginRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.security.JwtService;
import com.definex.finalcase.security.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_shouldReturnLoginResponseAndSetRefreshTokenCookie() {
        // Given
        var request = new LoginRequest("test@example.com", "password");
        var response = mock(HttpServletResponse.class);

        var authentication = mock(Authentication.class);
        var principal = mock(UserPrincipal.class);
        var userResponse = mock(UserResponse.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtService.issue(principal)).thenReturn("access-token");
        when(jwtService.issueRefreshToken(principal)).thenReturn("refresh-token");
        when(userService.getUserById(principal.getUserId())).thenReturn(userResponse);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        doNothing().when(response).addCookie(cookieCaptor.capture());

        // When
        var result = authenticationService.authenticate(request, response);

        // Then
        assertEquals("access-token", result.token());
        assertEquals(userResponse, result.user());

        Cookie addedCookie = cookieCaptor.getValue();
        assertEquals("r_t_", addedCookie.getName());
        assertEquals("refresh-token", addedCookie.getValue());
        assertEquals("/", addedCookie.getPath());
        assertTrue(addedCookie.isHttpOnly());
        assertTrue(addedCookie.getSecure());
        assertEquals(24 * 60 * 60, addedCookie.getMaxAge());
    }

    @Test
    void logout_shouldClearAuthenticationAndSetExpiredRefreshTokenCookie() {
        // Given
        HttpServletResponse response = mock(HttpServletResponse.class);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        doNothing().when(response).addCookie(cookieCaptor.capture());

        // When
        authenticationService.logout(response);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals("r_t_", cookie.getName());
        assertNull(cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
    }

    @Test
    void refresh_shouldReturnNewAccessToken_whenValidRefreshTokenProvided() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie refreshCookie = new Cookie("r_t_", "valid-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});

        UserPrincipal principalFromToken = mock(UserPrincipal.class);
        UserPrincipal fullPrincipal = mock(UserPrincipal.class);
        UserResponse userResponse = mock(UserResponse.class);

        when(jwtService.verifyToken("valid-refresh-token")).thenReturn(principalFromToken);
        when(principalFromToken.getEmail()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(fullPrincipal);
        when(jwtService.issue(fullPrincipal)).thenReturn("new-access-token");
        when(userService.getUserById(fullPrincipal.getUserId())).thenReturn(userResponse);

        // When
        var result = authenticationService.refresh(request);

        // Then
        assertEquals("new-access-token", result.token());
        assertEquals(userResponse, result.user());
    }

    @Test
    void refresh_shouldThrow_whenRefreshTokenCookieMissing() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // When - Then
        assertThrows(JWTVerificationException.class, () -> authenticationService.refresh(request));
    }

}

