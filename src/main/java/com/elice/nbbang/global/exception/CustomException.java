package com.elice.nbbang.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final int status;
    private final String message;

    public CustomException(final ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
