package com.elice.nbbang.domain.party.service.dto;

import lombok.Builder;

@Builder
public record PartyCreateServiceRequest(
        Long ottId,
        String ottAccountId,
        String ottAccountPassword
) {

}
