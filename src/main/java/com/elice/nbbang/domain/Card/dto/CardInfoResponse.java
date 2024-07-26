package com.elice.nbbang.domain.Card.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CardInfoResponse {

    private String issuerCorp;
    private String cardType;

}
