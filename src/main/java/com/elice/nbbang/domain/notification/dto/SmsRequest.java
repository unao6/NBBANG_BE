package com.elice.nbbang.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequest {

    String phoneNumber;
    String message;

    public SmsRequest(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }
}
