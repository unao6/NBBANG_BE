package com.elice.nbbang.domain.payment.dto;

import lombok.Data;

@Data
public class PaymentReserve {

    private String billingKey;

    private int amount;

    private String paymentTime;
}
