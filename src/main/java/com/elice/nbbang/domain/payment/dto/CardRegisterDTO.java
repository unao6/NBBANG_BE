package com.elice.nbbang.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardRegisterDTO {

    private String billingKey;

    private String cardNumber;

    private String cardCompany;
}
