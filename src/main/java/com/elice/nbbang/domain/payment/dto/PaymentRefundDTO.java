package com.elice.nbbang.domain.payment.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentRefundDTO {

    private Double amount;

    private int paymentAmount;       // 결제 금액
    private int refundAmount;        // 환불 금액
    private int oneDayPrice;         // 일일 가격
    private LocalDate paymentApprovedDate; // 결제 승인일
    private LocalDate refundRequestDate;   // 환불 요청일 (현재 날짜)
    private long daysUsed;           // 사용한 일수
    private double fee;              // 수수료
    private double amountUsed;       // 사용된 금액

    // amount를 제외한 생성자
    public PaymentRefundDTO(int paymentAmount, int refundAmount, int oneDayPrice, LocalDate paymentApprovedDate, LocalDate refundRequestDate, long daysUsed, double fee, double amountUsed) {
        this.paymentAmount = paymentAmount;
        this.refundAmount = refundAmount;
        this.oneDayPrice = oneDayPrice;
        this.paymentApprovedDate = paymentApprovedDate;
        this.refundRequestDate = refundRequestDate;
        this.daysUsed = daysUsed;
        this.fee = fee;
        this.amountUsed = amountUsed;
    }
}
