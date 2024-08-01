package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.service.dto.PartyCreateServiceRequest;

public record PartyCreateRequest(
        Long ottId,
        Long userId, // 시큐리티 구현이 되면 안받아도 될듯?
        String ottAccountId,
        String ottAccountPassword
) {
    public PartyCreateServiceRequest toServiceRequest() {
        return PartyCreateServiceRequest.builder()
                .ottId(ottId)
                .userId(userId)
                .ottAccountId(ottAccountId)
                .ottAccountPassword(ottAccountPassword)
                .build();
    }

}
