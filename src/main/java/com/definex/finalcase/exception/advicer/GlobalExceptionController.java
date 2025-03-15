package com.definex.finalcase.exception.advicer;

import com.definex.finalcase.exception.advicer.response.ErrorResponse;
import com.definex.finalcase.exception.base.BaseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorResponse> handleException(BaseException ex, WebRequest request) {
        var baseErrorMessage = ex.getBaseErrorMessage();
        return getErrorResponse(baseErrorMessage.getTitle(), baseErrorMessage.getMessage(), ex.getHttpStatus(), request);
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(String title, String message,
                                                           HttpStatus httpStatus, WebRequest request) {
        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .title(title)
                .message(message)
                .description(request.getDescription(false))
                .httpCode(httpStatus.value())
                .build();

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), httpStatus);
    }
}
