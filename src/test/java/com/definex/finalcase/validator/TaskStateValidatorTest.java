package com.definex.finalcase.validator;

import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.exception.InvalidStateTransitionException;
import com.definex.finalcase.exception.MissingReasonException;
import com.definex.finalcase.validation.TaskStateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskStateValidatorTest {

    private TaskStateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TaskStateValidator();
    }

    @Test
    void validateStateTransition_shouldSucceed_whenTransitionIsAllowedWithoutReason() {
        assertDoesNotThrow(() -> validator.validateStateTransition(
                TaskState.BACKLOG, TaskState.IN_ANALYSIS, null));
    }

    @Test
    void validateStateTransition_shouldSucceed_whenTransitionIsAllowedWithReason() {
        assertDoesNotThrow(() -> validator.validateStateTransition(
                TaskState.IN_DEVELOPMENT, TaskState.BLOCKED, "Build failure"));
    }

    @Test
    void validateStateTransition_shouldThrow_whenTransitionIsNotAllowed() {
        assertThrows(InvalidStateTransitionException.class, () ->
                validator.validateStateTransition(TaskState.COMPLETED, TaskState.BACKLOG, "Retry"));
    }

    @Test
    void validateStateTransition_shouldThrow_whenReasonIsMissingButRequired() {
        assertThrows(MissingReasonException.class, () ->
                validator.validateStateTransition(TaskState.IN_ANALYSIS, TaskState.BLOCKED, null));
    }

    @Test
    void validateStateTransition_shouldThrow_whenReasonIsBlank() {
        assertThrows(MissingReasonException.class, () ->
                validator.validateStateTransition(TaskState.BACKLOG, TaskState.CANCELLED, "   "));
    }

    @ParameterizedTest
    @CsvSource({
            "BACKLOG, IN_ANALYSIS, false",
            "BACKLOG, BLOCKED, true",
            "COMPLETED, BLOCKED, true",
            "IN_ANALYSIS, BLOCKED, false",
            "IN_DEVELOPMENT, CANCELLED, false",
            "BLOCKED, IN_DEVELOPMENT, false",
            "COMPLETED, BACKLOG, true"
    })
    void validateStateTransition_shouldBehaveAccordingToRules(
            TaskState from,
            TaskState to,
            boolean shouldThrow
    ) {
        String reason = (to == TaskState.CANCELLED || to == TaskState.BLOCKED) ? "reason" : null;

        if (shouldThrow) {
            assertThrows(Exception.class, () -> validator.validateStateTransition(from, to, reason));
        } else {
            assertDoesNotThrow(() -> validator.validateStateTransition(from, to, reason));
        }
    }
}