package com.definex.finalcase.domain.response;

import com.definex.finalcase.domain.enums.ProjectStatus;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProjectResponse(
        UUID id,
        String title,
        String description,
        String departmentName,
        ProjectStatus status,
        List<UUID> teamMembers
) {}
