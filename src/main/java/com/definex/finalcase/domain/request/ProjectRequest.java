package com.definex.finalcase.domain.request;

import com.definex.finalcase.domain.enums.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectRequest(
        String title,
        String description,
        String departmentName,
        ProjectStatus status
) {}
