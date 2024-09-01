package com.elice.nbbang.domain.party.controller.dto;

public record PartyMemberAdminResponse(
        String partyMemberEmail,
        String partyMemberNickname,
        String partyMemberPhoneNumber
) {
}
