package com.elice.nbbang.domain.party.service.dto;

import lombok.Builder;

@Builder
public record PartyMatchServiceRequest(
        Long userId,
        Long ottId
) {
}
