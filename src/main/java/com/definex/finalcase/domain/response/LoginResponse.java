package com.definex.finalcase.domain.response;

import lombok.Builder;

@Builder
public record LoginResponse (
        String token,
        UserResponse user
) {
}
