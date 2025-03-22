package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UserDuplicateInProjectException extends BaseException {
    public UserDuplicateInProjectException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.USER_ALREADY_EXISTS_IN_PROJECT);
    }
}
