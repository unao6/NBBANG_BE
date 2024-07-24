package com.elice.nbbang.domain.ott.dto;

public record OttCreateRequest(
        String name,
        int price,
        int capacity
) {
}
