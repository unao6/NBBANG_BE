package com.elice.nbbang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCheckRequestDto {
    private String phoneNumber;
    private String randomNumber;
}
