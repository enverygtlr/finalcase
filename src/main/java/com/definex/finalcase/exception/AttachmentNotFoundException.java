package com.definex.finalcase.exception;

import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class AttachmentNotFoundException extends BaseException {
    public AttachmentNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.ATTACHMENT_NOT_FOUND);
    }
}
