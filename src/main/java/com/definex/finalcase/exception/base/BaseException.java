package com.definex.finalcase.exception.base;

import com.definex.finalcase.exception.message.BaseErrorMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final BaseErrorMessage baseErrorMessage;

    public BaseException(HttpStatus httpStatus, BaseErrorMessage baseErrorMessage) {
        super(baseErrorMessage.getMessage());
        this.httpStatus = httpStatus;
        this.baseErrorMessage = baseErrorMessage;
    }
}
