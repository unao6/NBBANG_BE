package com.elice.nbbang.domain.user.dto;

import lombok.Getter;

@Getter
public class PhoneCheckRequestDto {
    private String phoneNumber;
    private String randomNumber;
}
