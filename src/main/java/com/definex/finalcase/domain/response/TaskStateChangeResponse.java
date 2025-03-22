package com.definex.finalcase.domain.response;

import com.definex.finalcase.domain.enums.TaskState;

import java.util.UUID;

public record TaskStateChangeResponse (
        UUID taskId,
        UUID changedByUserId,
        TaskState oldState,
        TaskState newState,
        String reason
) {
}
