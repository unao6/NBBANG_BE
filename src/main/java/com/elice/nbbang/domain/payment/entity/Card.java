package com.elice.nbbang.domain.payment.entity;

import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private User user;

    @Column(nullable = false)
    private String cardNumber; //암호화 필요

    @Column(nullable = false)
    private String cardHolder;

    @Column(nullable = false)
    private String cardCompany;

    @Column(nullable = false)
    private String billingKey; //암호화 필요
}