package com.elice.nbbang.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //OTT
    DUPLICATE_OTT_NAME(400, "중복된 OTT 서비스 입니다."),
    INVALID_CAPACITY(400, "잘못된 인원수 입니다."),
    NOT_FOUND_OTT(404, "조회된 OTT가 없습니다."),

    //Party
    NOT_FOUND_PARTY(404, "조회된 Party가 없습니다."),

    //Chat
    CHAT_NOT_FOUND(404, "채팅을 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    MESSAGE_SEND_ERROR(500, "메세지 전송 중 오류가 발생했습니다."),

    //Payment
    CARD_NOT_FOUND(404, "등록된 카드가 없습니다.");


    private final int status;
    private final String message;
}
