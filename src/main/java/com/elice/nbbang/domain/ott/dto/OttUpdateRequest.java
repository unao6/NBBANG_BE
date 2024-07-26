package com.elice.nbbang.domain.ott.dto;

public record OttUpdateRequest (
        Long ottId,
        String name,
        int price,
        int capacity
){
}
