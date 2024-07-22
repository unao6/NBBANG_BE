package com.elice.nbbang.domain.payment.entity;

import com.elice.nbbang.domain.payment.entity.enums.TransactionType;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Setter
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String cardCompany;

    private String bankName;
}