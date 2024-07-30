package com.elice.nbbang.domain.payment.entity;


import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
@Entity
@Builder(toBuilder = true)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String partnerUserId;

    private String partnerOrderId;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paymentCreatedAt;

    private String cid;

    private String tid;

    private LocalDateTime paymentApprovedAt;

    private LocalDateTime paymentSubscribedAt;

    private LocalDateTime refundDate;

    private String cardCompany;

    private Integer refundAmount;

    private String sid;

    private String bankName;

    private String billingKey;

    private String reserveId;

    public Payment(User user, String partnerUserId, String partnerOrderId, PaymentType paymentType, Integer amount,
        PaymentStatus status, LocalDateTime paymentCreatedAt, String cid, String tid) {
        this.user = user;
        this.partnerUserId = partnerUserId;
        this.partnerOrderId = partnerOrderId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = status;
        this.paymentCreatedAt = paymentCreatedAt;
        this.cid = cid;
        this.tid = tid;
    }

    public void updateApprovePayment(PaymentStatus status, String sid, LocalDateTime approvedAt) {
        this.status = status;
        this.sid = sid;
        this.paymentApprovedAt = approvedAt;
    }

    //todo 정보수정해야함
    public void updateRefundPayment(PaymentStatus status, String refundDate, String cardCompany, Integer refundAmount) {
        this.status = status;
        this.refundDate = LocalDateTime.parse(refundDate);
        this.cardCompany = cardCompany;
        this.refundAmount = refundAmount;
    }

    public void updateSubscribtionPayment(PaymentStatus status, LocalDateTime paymentSubscribedAt) {
        this.status = status;
        this.paymentSubscribedAt = paymentSubscribedAt;
    }

    public PaymentReserve toPaymentReserve() {
        PaymentReserve paymentReserve = new PaymentReserve();
        paymentReserve.setBillingKey(this.getBillingKey());
        paymentReserve.setAmount(this.getAmount());
        paymentReserve.setPaymentSubscribedAt(this.getPaymentSubscribedAt());

        return paymentReserve;
    }
}
