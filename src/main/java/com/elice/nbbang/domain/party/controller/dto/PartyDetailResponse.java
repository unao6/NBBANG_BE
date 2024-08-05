package com.elice.nbbang.domain.party.controller.dto;

import com.elice.nbbang.domain.party.entity.Party;

import java.util.List;

public record PartyDetailResponse(
        Long partyId,
        boolean isLeader,
        Long ottId,
        String ottName,
        int ottPrice,
        int capacity,
        String ottAccountId,
        String ottAccountPassword,
        Long leaderId,
        String leaderNickname,
        List<PartyMemberResponse> members
        // 날짜도 넣어야 되나? 생각한번 하자
) {

    public PartyDetailResponse(Party party, boolean isLeader) {
        this(
                party.getId(),
                isLeader,
                party.getOtt().getId(),
                party.getOtt().getName(),
                party.getOtt().getPrice(),
                party.getOtt().getCapacity(),
                party.getOttAccountId(),
                party.getOttAccountPassword(),
                party.getLeader().getId(),
                party.getLeader().getNickname(),
                party.getPartyMembers()
                        .stream()
                        .map(m -> new PartyMemberResponse(
                                m.getId(),
                                m.getUser().getNickname()
                        ))
                        .toList()
        );
    }
}
