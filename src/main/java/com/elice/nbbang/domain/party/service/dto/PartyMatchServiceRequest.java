package com.elice.nbbang.domain.party.service.dto;

import com.elice.nbbang.domain.party.entity.MatchingType;
import lombok.Builder;

@Builder
public record PartyMatchServiceRequest(
        Long ottId
) {
}
