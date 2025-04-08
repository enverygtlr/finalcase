package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.request.UserUpdateRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.security.UserPrincipal;
import com.definex.finalcase.security.permission.AnyTeamRole;
import com.definex.finalcase.security.permission.ProjectManagerRole;
import com.definex.finalcase.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ProjectManagerRole
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AnyTeamRole
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @AnyTeamRole
    @PatchMapping
    public ResponseEntity<UserResponse> updateUserInformation(@AuthenticationPrincipal UserPrincipal user, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateUser(user.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @PatchMapping("/{id}/role/{role}")
    public ResponseEntity<UserResponse> updateUserRole(UUID id, @PathVariable Role role) {
        UserResponse response = userService.updateUserRole(id, role);
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable UUID id) {
        UserResponse response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
}