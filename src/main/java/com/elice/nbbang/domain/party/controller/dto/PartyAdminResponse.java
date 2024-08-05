package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.entity.Party;

import java.util.List;

public record PartyAdminResponse(
        String ottName,
        String leaderEmail,
        String leaderNickname,
        String leaderPhoneNumber,
        List<PartyMemberAdminResponse> members


) {
    public PartyAdminResponse (Party party) {
        this(
                party.getOtt().getName(),
                party.getLeader().getEmail(),
                party.getLeader().getNickname(),
                party.getLeader().getPhoneNumber(),
                party.getPartyMembers()
                        .stream()
                        .map(m -> new PartyMemberAdminResponse(
                                m.getUser().getEmail(),
                                m.getUser().getNickname(),
                                m.getUser().getPhoneNumber()

                        ))
                        .toList()
        );

    }
}
