package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.service.dto.PartyUpdateServiceRequest;

public record PartyUpdateRequest(
        String ottAccountId,
        String ottAccountPassword

) {
    public PartyUpdateServiceRequest toServiceRequest() {
        return PartyUpdateServiceRequest.builder()
                .ottAccountId(ottAccountId)
                .ottAccountPassword(ottAccountPassword)
                .build();
    }
}
