package com.definex.finalcase.exception.advicer;

import com.definex.finalcase.exception.advicer.response.ErrorResponse;
import com.definex.finalcase.exception.base.BaseException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
@Hidden
public class GlobalExceptionController {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorResponse> handleException(BaseException ex, WebRequest request) {
        var baseErrorMessage = ex.getBaseErrorMessage();
        return getErrorResponse(baseErrorMessage.getTitle(), baseErrorMessage.getMessage(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return getErrorResponse(
                "Access Denied",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return getErrorResponse(
                "Unexpected Error",
                ex.getMessage() != null ? ex.getMessage() : "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return getErrorResponse(
                "Runtime Error",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
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
