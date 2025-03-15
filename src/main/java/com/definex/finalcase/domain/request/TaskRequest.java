package com.definex.finalcase.domain.request;

import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import lombok.Builder;

@Builder
public record TaskRequest(
        String title,
        String description,
        String acceptanceCriteria,
        TaskState state,
        TaskPriority priority
) {}