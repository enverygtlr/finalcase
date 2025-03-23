package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.request.UserUpdateRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.UserMapper;
import com.definex.finalcase.repository.UserRepository;
import com.definex.finalcase.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSucceed() {
        // Given
        var request = UserRequest.builder()
                .name("Enver")
                .email("enver@example.com")
                .password("secret")
                .role(Role.TEAM_MEMBER)
                .build();

        var user = new User();
        var savedUser = new User();
        var expectedResponse = Mockito.mock(UserResponse.class);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // When
        var actual = userService.createUser(request);

        // Then
        assertEquals(expectedResponse, actual);
        verify(userMapper).toEntity(request);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() {
        // Given
        UUID id = UUID.randomUUID();
        var user = new User();
        var expected = Mockito.mock(UserResponse.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        // When
        var actual = userService.getUserById(id);

        // Then
        assertEquals(expected, actual);
        verify(userRepository).findById(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserById_shouldThrow_whenUserDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository).findById(id);
    }

    @Test
    void getUserByEmail_shouldReturnPrincipal_whenUserExists() {
        // Given
        String email = "test@example.com";
        var user = new User();
        var expected = Mockito.mock(UserPrincipal.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.convertToUserPrincipal(user)).thenReturn(expected);

        // When
        var actual = userService.getUserByEmail(email);

        // Then
        assertEquals(expected, actual);
        verify(userRepository).findByEmail(email);
        verify(userMapper).convertToUserPrincipal(user);
    }

    @Test
    void getUserByEmail_shouldThrow_whenUserNotFound() {
        // Given
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getAllUsers_shouldReturnMappedResponses() {
        // Given
        var user1 = new User();
        var user2 = new User();
        var users = List.of(user1, user2);

        var response1 = Mockito.mock(UserResponse.class);
        var response2 = Mockito.mock(UserResponse.class);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponse(user1)).thenReturn(response1);
        when(userMapper.toResponse(user2)).thenReturn(response2);

        // When
        var result = userService.getAllUsers();

        // Then
        assertEquals(List.of(response1, response2), result);
        verify(userRepository).findAll();
    }

    @Test
    void updateUserRole_shouldSucceed_whenUserExists() {
        // Given
        UUID id = UUID.randomUUID();
        Role newRole = Role.TEAM_LEADER;

        var user = new User();
        var updatedUser = new User();
        var expected = Mockito.mock(UserResponse.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expected);

        // When
        var actual = userService.updateUserRole(id, newRole);

        // Then
        assertEquals(expected, actual);
        assertEquals(newRole, user.getRole());
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(updatedUser);
    }

    @Test
    void updateUserRole_shouldThrow_whenUserNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole(id, Role.PROJECT_MANAGER));
        verify(userRepository).findById(id);
    }

    @Test
    void updateUser_shouldSucceed_whenUserExists() {
        // Given
        UUID id = UUID.randomUUID();
        var request = UserUpdateRequest.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .password("newpassword")
                .build();

        var user = new User();
        var savedUser = new User();
        var expected = Mockito.mock(UserResponse.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expected);

        // When
        var actual = userService.updateUser(id, request);

        // Then
        assertEquals(expected, actual);
        assertEquals("Updated Name", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("newpassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        var request = Mockito.mock(UserUpdateRequest.class);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));
        verify(userRepository).findById(id);
    }
}