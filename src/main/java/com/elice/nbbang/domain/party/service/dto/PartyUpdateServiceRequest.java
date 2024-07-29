package com.elice.nbbang.domain.party.service.dto;

import lombok.Builder;

@Builder
public record PartyUpdateServiceRequest(
        String ottAccountId,
        String ottAccountPassword
) {

}
