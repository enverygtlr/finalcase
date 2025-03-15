package com.definex.finalcase.exception.message;

import java.io.Serializable;

public interface BaseErrorMessage extends Serializable {
    String getTitle();
    String getMessage();
}