package com.elice.nbbang.domain.ott.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class DuplicateOttName extends CustomException {

    public DuplicateOttName(ErrorCode errorCode) {
        super(errorCode);
    }
}
