package com.definex.finalcase.domain.response;

import com.definex.finalcase.domain.enums.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse (
        UUID id,
        String name,
        String email,
        Role role
) {}
