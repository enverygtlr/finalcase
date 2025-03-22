package com.definex.finalcase.controller;

import com.definex.finalcase.domain.request.LoginRequest;
import com.definex.finalcase.domain.response.LoginResponse;
import com.definex.finalcase.security.permission.AnyTeamRole;
import com.definex.finalcase.security.permission.PublicEndpoint;
import com.definex.finalcase.security.permission.RefreshPermit;
import com.definex.finalcase.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PublicEndpoint
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest, response));
    }

    @RefreshPermit
    @GetMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.refresh(request));
    }

    @AnyTeamRole
    @GetMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        authenticationService.logout(response);
        return ResponseEntity.ok().build();
    }
}
