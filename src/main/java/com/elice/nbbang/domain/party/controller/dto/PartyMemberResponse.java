package com.elice.nbbang.domain.party.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PartyMemberResponse(
        Long partyMemberId,
        Long userId,
        String partyMemberNickname,

        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime joinDate
        // 날짜도 넣어야 하나? 생각 하자
) {
}
