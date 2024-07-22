package com.elice.nbbang.domain.payment.entity;


import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private User user;

    private String paymentType;

    private LocalDateTime paymentDate;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String cardCompany;
}
