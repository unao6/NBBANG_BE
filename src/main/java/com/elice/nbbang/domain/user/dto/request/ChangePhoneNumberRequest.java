package com.elice.nbbang.domain.user.dto.request;

import lombok.Data;

@Data
public class ChangePhoneNumberRequest {

    private String newPhoneNumber;
    private String verificationCode;

}