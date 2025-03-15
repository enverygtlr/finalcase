package com.definex.finalcase.exception.advicer.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse (
        @NotNull LocalDateTime timestamp,
        String title,
        @NotNull String message,
        @NotNull String description,
        @NotNull int httpCode
) {
}
