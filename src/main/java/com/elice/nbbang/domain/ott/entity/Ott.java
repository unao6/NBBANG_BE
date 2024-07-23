package com.elice.nbbang.domain.ott.entity;

import com.elice.nbbang.domain.ott.exception.InvalidOttCapacity;
import com.elice.nbbang.global.exception.ErrorCode;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Generated;
import lombok.Getter;

@Entity
@Getter
public class Ott extends BaseTimeEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ott_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int perMonthPrice;

    @Column(nullable = false)
    private int capacity;

    public static Ott of(String name, int perMonthPrice, int capacity) {
        Ott ott = new Ott();
        ott.name = name;
        ott.perMonthPrice = perMonthPrice;
        ott.capacity = capacity;

        return ott;
    }

    public void updateOtt(String name, int perMonthPrice, int capacity) {
        this.name = name;
        this.perMonthPrice = perMonthPrice;
        this.capacity = capacity;
    }

}
