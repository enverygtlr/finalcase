package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class MissingReasonException extends BaseException {
    public MissingReasonException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.MISSING_REASON);
    }
}
