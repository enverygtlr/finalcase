package com.definex.finalcase.exception.message;

import lombok.Getter;

@Getter
public enum ErrorMessage implements BaseErrorMessage {

    USER_NOT_FOUND("User not found", "User Error"),
    USER_ALREADY_EXISTS("User with this email already exists", "User Error"),

    PROJECT_NOT_FOUND("Project not found", "Project Error"),
    PROJECT_ALREADY_EXISTS("A project with this title already exists", "Project Error"),

    TASK_NOT_FOUND("Task not found", "Task Error"),
    TASK_ALREADY_COMPLETED("Cannot modify a completed task", "Task Error"),
    INVALID_TASK_STATE("Invalid task state transition", "Task Error");


    private final String title;
    private final String message;

    ErrorMessage(String message, String title) {
        this.message = message;
        this.title = title;
    }

    @Override
    public String toString() {
        return title + " | " + message;
    }
}
