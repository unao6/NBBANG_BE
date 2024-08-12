package com.elice.nbbang.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONUtil;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCheckRequestDto {

    private String phoneNumber;
    private String randomNumber;

}
