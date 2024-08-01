package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.service.dto.PartyMatchServiceRequest;
import lombok.Builder;

public record PartyMatchRequest(
        Long userId,
        Long ottId
){
    public PartyMatchServiceRequest toServiceRequest() {
        return PartyMatchServiceRequest.builder()
                .ottId(ottId)
                .userId(userId)
                .build();
    }
}
