package com.definex.finalcase.exception.message;

import lombok.Getter;

@Getter
public enum ErrorMessage implements BaseErrorMessage {

    USER_NOT_FOUND("User not found", "User Error"),
    INVALID_REQUEST("Invalid request", "Validation Error");

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
