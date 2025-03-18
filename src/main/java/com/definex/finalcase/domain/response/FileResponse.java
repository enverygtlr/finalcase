package com.definex.finalcase.domain.response;

import lombok.Builder;

@Builder
public record FileResponse(
        String fileName,
        String mimeType,
        byte[] data
) {
}
