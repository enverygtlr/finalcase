package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.COMMENT_NOT_FOUND);
    }
}
