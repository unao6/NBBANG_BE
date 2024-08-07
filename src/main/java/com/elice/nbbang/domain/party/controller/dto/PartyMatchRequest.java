package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.entity.MatchingType;
import com.elice.nbbang.domain.party.service.dto.PartyMatchServiceRequest;
import lombok.Builder;

public record PartyMatchRequest(
        Long ottId
){
    public PartyMatchServiceRequest toServiceRequest() {
        return PartyMatchServiceRequest.builder()
                .ottId(ottId)
                .build();
    }

}
