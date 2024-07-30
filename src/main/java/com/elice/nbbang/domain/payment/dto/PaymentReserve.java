package com.elice.nbbang.domain.payment.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentReserve {

    private String billingKey;

    private Integer amount;

    private LocalDateTime paymentSubscribedAt;
}
