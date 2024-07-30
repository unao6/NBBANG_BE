package com.elice.nbbang.domain.payment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardInfoResponse {

    private String issuerCorp;
    private String cardType;
    private String cardCompany;

}
