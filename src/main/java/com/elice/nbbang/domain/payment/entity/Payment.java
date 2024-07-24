package com.elice.nbbang.domain.payment.entity;


import com.elice.nbbang.domain.payment.enums.PaymentStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
@Entity
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String partnerUserId;


    @Column(nullable = false)
    private String partnerOrderId;

    @Column(nullable = false)
    private String paymentType;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column
    private LocalDateTime refundDate;

    @Column
    private String cardCompany;

    @Column
    private Long refundAmount;

    @Column(nullable = false)
    private String cid;

    @Column(nullable = false)
    private String tid;

    @Column(nullable = false)
    private String sid;

    public Payment(User user, String partnerUserId, String partnerOrderId, String paymentType, Long amount,
        PaymentStatus status, LocalDateTime paymentDate, String cid, String tid, String sid) {
        this.user = user;
        this.partnerUserId = partnerUserId;
        this.partnerOrderId = partnerOrderId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.cid = cid;
        this.tid = tid;
        this.sid = sid;
    }
}
