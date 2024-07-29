package com.elice.nbbang.domain.ott.service.dto;

import lombok.Builder;

@Builder
public record OttUpdateServiceRequest(
        Long ottId,
        String name,
        int price,
        int capacity
) {
}
