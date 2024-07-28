package com.elice.nbbang.domain.party.service.dto;

import lombok.Builder;

@Builder
public record PartyCreateServiceRequest(
        Long ottId,
        Long userId, // 시큐리티 구현이 되면 안받아도 될듯?
        String ottAccountId,
        String ottAccountPassword
) {

}
