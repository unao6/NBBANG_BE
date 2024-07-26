package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRegisterDTO {

    private String bankName;

    private Long amount;

    private String billingKey;

    private LocalDateTime paymentDate;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private String reserveId;
}
