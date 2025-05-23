package com.definex.finalcase.domain.request;

import com.definex.finalcase.domain.enums.Role;
import lombok.Builder;

@Builder
public record UserRequest (
        String name,
        String email,
        String password,
        Role role
) {}

