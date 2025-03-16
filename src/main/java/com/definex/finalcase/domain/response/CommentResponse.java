package com.definex.finalcase.domain.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponse(
        UUID id,
        UUID taskId,
        UUID userId,
        String content,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {}