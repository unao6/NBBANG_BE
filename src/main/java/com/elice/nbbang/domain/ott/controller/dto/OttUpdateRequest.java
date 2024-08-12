package com.elice.nbbang.domain.ott.controller.dto;

import com.elice.nbbang.domain.ott.service.dto.OttUpdateServiceRequest;

public record OttUpdateRequest (
        String name,
        int price,
        int capacity
){
    public OttUpdateServiceRequest toServiceRequest() {
        return OttUpdateServiceRequest.builder()
                .name(name)
                .price(price)
                .capacity(capacity)
                .build();
    }

}
