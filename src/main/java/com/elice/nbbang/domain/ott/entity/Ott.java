package com.elice.nbbang.domain.ott.entity;


import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    /*
    * 수수료 생각해서 OTT 구독료로 세팅 price
    * */
    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int capacity;

    public static Ott of(String name, int price, int capacity) {
        Ott ott = new Ott();
        ott.name = name;
        ott.price = price;
        ott.capacity = capacity;

        return ott;
    }

    public void updateOtt(String name, int price, int capacity) {
        this.name = name;
        this.price = price;
        this.capacity = capacity;
    }

}
