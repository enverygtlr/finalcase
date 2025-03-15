package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends BaseException {
    public TaskNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.TASK_NOT_FOUND);
    }
}
