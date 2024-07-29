package com.elice.nbbang.domain.ott.controller.dto;

import com.elice.nbbang.domain.ott.service.dto.OttCreateServiceRequest;

public record OttCreateRequest(
        String name,
        int price,
        int capacity
) {
    public OttCreateServiceRequest toServiceRequest() {
        return OttCreateServiceRequest.builder()
                .name(name)
                .price(price)
                .capacity(capacity)
                .build();
    }

}
