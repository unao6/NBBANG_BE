package com.elice.nbbang.domain.chat.exception;

import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;

public class ChatNotFoundException extends CustomException {
    public ChatNotFoundException() {
        super(ErrorCode.CHAT_NOT_FOUND);
    }
}
