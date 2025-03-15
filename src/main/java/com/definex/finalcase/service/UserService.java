package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.exception.UserNotFound;
import com.definex.finalcase.mapper.UserMapper;
import com.definex.finalcase.repository.UserRepository;
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
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new UserNotFound());
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findActiveUsers()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new UserNotFound());

        user.setName(request.name());
        user.setEmail(request.email());
        return userMapper.toResponse(userRepository.save(user));
    }
}
