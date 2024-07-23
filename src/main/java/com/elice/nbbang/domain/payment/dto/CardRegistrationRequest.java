package com.elice.nbbang.domain.payment.dto;

import lombok.Data;

@Data
public class CardRegistrationRequest {

    private String cardNumber;

    private String cardHolder;

    private String cardCompany;

    private String billingKey;

    private String expirationYear;

    private String expirationMonth;

    private String birthDate;

    private String securityCode;
}