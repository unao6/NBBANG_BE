package com.elice.nbbang.domain.user.dto.request;

import lombok.Data;

@Data
public class PhoneNumberChangeRequestDto {

    private String newPhoneNumber;
    private String randomNumber;

}