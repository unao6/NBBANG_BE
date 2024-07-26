package com.elice.nbbang.domain.ott.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class InvalidOttCapacity extends CustomException {
    public InvalidOttCapacity(ErrorCode errorCode) {
        super(errorCode);
    }
}
