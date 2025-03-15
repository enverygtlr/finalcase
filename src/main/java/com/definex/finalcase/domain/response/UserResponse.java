package com.definex.finalcase.domain.response;

import com.definex.finalcase.domain.enums.Role;

import java.util.UUID;

public record UserResponse (
        UUID id,
        String name,
        String email,
        Role role
) {}
