package com.definex.finalcase.domain.response;

import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TaskResponse(
        UUID id,
        String title,
        String description,
        String acceptanceCriteria,
        TaskState state,
        TaskPriority priority,
        UUID projectId,
        UUID assigneeId
) {}