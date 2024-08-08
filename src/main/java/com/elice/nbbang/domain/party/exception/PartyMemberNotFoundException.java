package com.elice.nbbang.domain.party.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class PartyMemberNotFoundException extends CustomException {

    public PartyMemberNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
