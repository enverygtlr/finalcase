package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.request.UserUpdateRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.security.UserPrincipal;
import com.definex.finalcase.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void createUser_shouldSucceedForProjectManager() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Enver")
                .email("enver@example.com")
                .password("123")
                .role(Role.TEAM_MEMBER)
                .build();

        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .name("Enver")
                .email("enver@example.com")
                .role(Role.TEAM_MEMBER)
                .build();

        when(userService.createUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("enver@example.com"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void createUser_shouldReturnForbiddenForTeamMember() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Ali")
                .email("ali@example.com")
                .password("123")
                .role(Role.TEAM_LEADER)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getUserById_shouldSucceedForTeamMember() throws Exception {
        UUID id = UUID.randomUUID();

        UserResponse response = UserResponse.builder()
                .id(id)
                .name("Ali")
                .email("ali@example.com")
                .role(Role.TEAM_MEMBER)
                .build();

        when(userService.getUserById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ali@example.com"));
    }

    @Test
    void updateUserInformation_shouldSucceedForAuthenticatedUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .email("test@mail.com")
                .password("123")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")))
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("Updated")
                .email("new@mail.com")
                .password("pass")
                .build();

        UserResponse response = UserResponse.builder()
                .id(userId)
                .name("Updated")
                .email("new@mail.com")
                .role(Role.TEAM_MEMBER)
                .build();

        when(userService.updateUser(userId, request)).thenReturn(response);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When - Then
        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void updateUserInformation_shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("New")
                .email("new@mail.com")
                .password("pass")
                .build();

        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
