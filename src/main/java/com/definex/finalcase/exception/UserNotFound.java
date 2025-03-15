package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class UserNotFound extends BaseException {
    public UserNotFound() {super(HttpStatus.CONFLICT, ErrorMessage.USER_NOT_FOUND);}
}
