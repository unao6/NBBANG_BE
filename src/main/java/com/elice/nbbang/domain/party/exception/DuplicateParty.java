package com.elice.nbbang.domain.party.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class DuplicateParty extends CustomException {
    public DuplicateParty(ErrorCode errorCode) {
        super(errorCode);
    }
}
