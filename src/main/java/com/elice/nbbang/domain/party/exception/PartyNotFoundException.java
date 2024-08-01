package com.elice.nbbang.domain.party.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class PartyNotFoundException extends CustomException {

    public PartyNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
