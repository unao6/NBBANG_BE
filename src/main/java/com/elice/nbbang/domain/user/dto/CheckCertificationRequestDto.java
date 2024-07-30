package com.elice.nbbang.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckCertificationRequestDto {

    private String email;
    private String certificationNumber;
}
