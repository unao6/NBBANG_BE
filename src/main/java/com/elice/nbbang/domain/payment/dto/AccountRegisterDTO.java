package com.elice.nbbang.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisterDTO {

    private String accountNumber;

    private String bankName;
}
