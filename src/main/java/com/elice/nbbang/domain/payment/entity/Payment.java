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
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    @Setter
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paymentCreatedAt;

    private String cid;

    private String tid;

    private Long ottId;

    private LocalDateTime paymentApprovedAt;

    private LocalDateTime paymentSubscribedAt;

    private LocalDateTime refundDate;

    private int installmentNumber;

    private String cardCompany;

    private Integer refundAmount;

    private String sid;

    private String bankName;

    private String billingKey;

    private String reserveId;

    private String receiptId;

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

    public void updateSubscribtionPayment(PaymentStatus status, LocalDateTime paymentSubscribedAt) {
        this.status = status;
        this.paymentSubscribedAt = paymentSubscribedAt;
    }

    public void updatePaymentSubscribedAt(LocalDateTime paymentSubscribedAt){
        this.paymentSubscribedAt = paymentSubscribedAt;
    }

    public void updateRefundPayment(PaymentStatus status, Integer refundAmount, LocalDateTime refundDate) {
        this.status = status;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
    }

    public void updateCompletePayment(PaymentStatus status, LocalDateTime paymentApprovedAt, String receiptId) {
        this.status = status;
        this.paymentApprovedAt = paymentApprovedAt;
        this.receiptId = receiptId;
    }

    public void updateFailurePayment(PaymentStatus status) {
        this.status = status;
    }
}
