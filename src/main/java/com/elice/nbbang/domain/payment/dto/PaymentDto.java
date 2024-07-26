package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.enums.PaymentStatus;
import com.elice.nbbang.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDto {
    private Long id;
    private String tid;
    private String partnerUserId;
    private String partnerOrderId;
    private String paymentType;
    private Integer amount;
    private String status;
    private String paymentCreatedAt;
    private String cid;
    private String paymentApprovedAt;
    private String refundDate;
    private String cardCompany;
    private Integer refundAmount;
    private String sid;

    public static PaymentDto fromEntity(Payment payment) {
        return PaymentDto.builder()
            .id(payment.getId())
            .tid(payment.getTid())
            .partnerUserId(payment.getPartnerUserId())
            .partnerOrderId(payment.getPartnerOrderId())
            .paymentType(payment.getPaymentType())
            .amount(payment.getAmount())
            .status(payment.getStatus().toString())
            .paymentCreatedAt(payment.getPaymentCreatedAt() != null ? payment.getPaymentCreatedAt().toString() : null)
            .cid(payment.getCid())
            .paymentApprovedAt(payment.getPaymentApprovedAt() != null ? payment.getPaymentApprovedAt().toString() : null)
            .refundDate(payment.getRefundDate() != null ? payment.getRefundDate().toString() : null)
            .cardCompany(payment.getCardCompany())
            .refundAmount(payment.getRefundAmount())
            .sid(payment.getSid())
            .build();
    }
}
