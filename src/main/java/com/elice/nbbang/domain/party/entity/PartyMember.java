package com.elice.nbbang.domain.party.entity;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PartyMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ott_id")
    private Ott ott;

    private LocalDateTime joinDate;

    private LocalDateTime expirationDate;

    private LocalDateTime breakUpDate;

    public static PartyMember of(User user, Party party, Ott ott, LocalDateTime joinDate) {

        PartyMember partyMember = new PartyMember();
        partyMember.user = user;
        partyMember.setParty(party);
        partyMember.ott = ott;
        partyMember.joinDate = joinDate;
        partyMember.expirationDate = joinDate.plusMonths(1);

        return partyMember;
    }

    public void setParty(Party party) {
        this.party = party;
        if (!party.getPartyMembers().contains(this)) {
            party.getPartyMembers().add(this);
        }
    }
    public void addBreakUpDate(LocalDateTime breakUpDate) {
        this.breakUpDate = breakUpDate;
    }

    public void withdrawParty() {
        if (this.party != null) {
            this.party.getPartyMembers().remove(this);
            this.party = null;
        }
    }

    public void plusExpiration() {
        this.expirationDate = LocalDateTime.now().plusMonths(1);
    }


}
