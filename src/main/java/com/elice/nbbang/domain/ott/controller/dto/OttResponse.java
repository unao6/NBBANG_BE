package com.elice.nbbang.domain.ott.controller.dto;

public record OttResponse(
        Long ottId,
        String name,
        int price,
        int capacity
) {

}
