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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return userMapper.toResponse(user);
    }

    public UserPrincipal getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return userMapper.convertToUserPrincipal(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }


    public UserResponse updateUserRole(UUID id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.setRole(role);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        return userMapper.toResponse(userRepository.save(user));
    }
}
