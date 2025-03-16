package com.definex.finalcase.exception;

import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.exception.base.BaseException;
import com.definex.finalcase.exception.message.ErrorMessage;
import org.springframework.http.HttpStatus;

public class InvalidStateTransitionException extends BaseException {
  public InvalidStateTransitionException() {
    super(HttpStatus.CONFLICT, ErrorMessage.INVALID_TASK_STATE);
  }
}
