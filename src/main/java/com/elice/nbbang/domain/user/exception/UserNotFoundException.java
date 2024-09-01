package com.elice.nbbang.domain.user.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
