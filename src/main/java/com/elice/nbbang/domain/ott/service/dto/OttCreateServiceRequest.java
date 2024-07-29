package com.elice.nbbang.domain.ott.service.dto;

import lombok.Builder;

@Builder
public record OttCreateServiceRequest(
        String name,
        int price,
        int capacity
) {
}
