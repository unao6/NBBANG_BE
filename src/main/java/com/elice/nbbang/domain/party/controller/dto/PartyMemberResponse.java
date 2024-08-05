package com.elice.nbbang.domain.party.controller.dto;

public record PartyMemberResponse(
        Long partyMemberId,
        String partyMemberNickname
        // 날짜도 넣어야 하나? 생각 하자
) {
}
