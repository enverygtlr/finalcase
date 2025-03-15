package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends BaseException {
    public ProjectNotFoundException() {
        super(HttpStatus.NOT_FOUND,ErrorMessage.PROJECT_NOT_FOUND);
    }
}
