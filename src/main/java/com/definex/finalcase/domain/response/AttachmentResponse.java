package com.definex.finalcase.domain.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AttachmentResponse(
        UUID id,
        UUID taskId,
        UUID uploadedById,
        String fileName,
        String contentType,
        String filePath,
        LocalDateTime createDate
) {}