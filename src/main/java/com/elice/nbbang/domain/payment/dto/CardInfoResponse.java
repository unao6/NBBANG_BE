package com.elice.nbbang.domain.payment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoResponse {

    private String issuerCorp;
    private String cardType;
    private String cardCompany;
    private String cardNumber;
}
