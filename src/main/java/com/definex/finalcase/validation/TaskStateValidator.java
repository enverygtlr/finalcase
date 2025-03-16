package com.definex.finalcase.validation;

import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.exception.InvalidStateTransitionException;
import com.definex.finalcase.exception.MissingReasonException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TaskStateValidator {

    private static final Map<TaskState, List<TaskState>> ALLOWED_TRANSITIONS = Map.of(
            TaskState.BACKLOG, List.of(TaskState.IN_ANALYSIS, TaskState.CANCELLED),
            TaskState.IN_ANALYSIS, List.of(TaskState.BACKLOG, TaskState.IN_DEVELOPMENT, TaskState.BLOCKED, TaskState.CANCELLED),
            TaskState.IN_DEVELOPMENT, List.of(TaskState.IN_ANALYSIS, TaskState.COMPLETED, TaskState.BLOCKED, TaskState.CANCELLED),
            TaskState.BLOCKED, List.of(TaskState.IN_ANALYSIS, TaskState.IN_DEVELOPMENT, TaskState.CANCELLED),
            TaskState.COMPLETED, List.of(),
            TaskState.CANCELLED, List.of()
    );

    private static final List<TaskState> REQUIRES_REASON = List.of(TaskState.CANCELLED, TaskState.BLOCKED);

    public void validateStateTransition(TaskState currentState, TaskState newState, String reason) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(currentState, List.of()).contains(newState)) {
            throw new InvalidStateTransitionException();
        }

        if (REQUIRES_REASON.contains(newState) && (reason == null || reason.isBlank())) {
            throw new MissingReasonException();
        }
    }
}