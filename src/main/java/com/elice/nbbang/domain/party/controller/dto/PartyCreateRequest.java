package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.service.dto.PartyCreateServiceRequest;

public record PartyCreateRequest(
        Long ottId,
        String ottAccountId,
        String ottAccountPassword
) {
    public PartyCreateServiceRequest toServiceRequest() {
        return PartyCreateServiceRequest.builder()
                .ottId(ottId)
                .ottAccountId(ottAccountId)
                .ottAccountPassword(ottAccountPassword)
                .build();
    }

}
