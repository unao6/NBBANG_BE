package com.elice.nbbang.domain.ott.controller.dto;

import com.elice.nbbang.domain.ott.service.dto.OttUpdateServiceRequest;

public record OttUpdateRequest (
        Long ottId,
        String name,
        int price,
        int capacity
){
    public OttUpdateServiceRequest toServiceRequest() {
        return OttUpdateServiceRequest.builder()
                .ottId(ottId)
                .name(name)
                .price(price)
                .capacity(capacity)
                .build();
    }

}
