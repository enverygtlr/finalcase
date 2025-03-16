package com.definex.finalcase.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CommentRequest(
        @NotBlank String content
) {}
