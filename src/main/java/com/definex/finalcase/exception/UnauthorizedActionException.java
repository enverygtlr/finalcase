package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BaseException {
    public UnauthorizedActionException() {
        super(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACTION);
    }
}
