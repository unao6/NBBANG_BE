package com.elice.nbbang.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckCertificationRequestDto {

    private String email;
    private String certificationNumber;

}
