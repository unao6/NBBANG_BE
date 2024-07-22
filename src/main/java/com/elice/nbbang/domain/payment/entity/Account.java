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
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private User user;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankName;
}