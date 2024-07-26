package com.elice.nbbang.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //OTT
    DUPLICATE_OTT_NAME(400, "중복된 OTT 서비스 입니다."),
    INVALID_CAPACITY(400, "잘못된 인원수 입니다."),
    NOT_FOUND_OTT(404, "조회된 OTT가 없습니다.");


    private final int status;
    private final String message;
}
