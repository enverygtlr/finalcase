package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.LoginRequest;
import com.definex.finalcase.domain.response.LoginResponse;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_shouldReturnTokenAndUserInfo_whenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "123456");

        LoginResponse response = LoginResponse.builder()
                .token("jwt-token")
                .user(UserResponse.builder()
                        .id(UUID.randomUUID())
                        .email("user@example.com")
                        .name("User")
                        .role(Role.TEAM_MEMBER)
                        .build())
                .build();

        when(authenticationService.authenticate(any(LoginRequest.class), any(HttpServletResponse.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    @WithMockUser(authorities = "REFRESH")
    void refresh_shouldReturnNewToken_whenValidRefreshTokenProvided() throws Exception {
        LoginResponse response = LoginResponse.builder()
                .token("refreshed-jwt-token")
                .user(UserResponse.builder()
                        .id(UUID.randomUUID())
                        .email("user@example.com")
                        .name("User")
                        .role(Role.TEAM_MEMBER)
                        .build())
                .build();

        when(authenticationService.refresh(any(HttpServletRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("refreshed-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void logout_shouldSucceedForAuthenticatedUser() throws Exception {
        doNothing().when(authenticationService).logout(any(HttpServletResponse.class));

        mockMvc.perform(get("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}

