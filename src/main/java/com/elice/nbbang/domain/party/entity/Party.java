package com.elice.nbbang.domain.party.entity;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.party.service.dto.PartyUpdateServiceRequest;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Party extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ott_id")
    private Ott ott;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Column(nullable = false)
    private String ottAccountId;

    @Column(nullable = false)
    private String ottAccountPassword;

    @Enumerated(EnumType.STRING)
    private PartyStatus partyStatus;

    @OneToMany(mappedBy = "party")
    private List<PartyMember> partyMembers = new ArrayList<>();

    private LocalDateTime settlementDate;

    @Builder
    public Party(Ott ott, String ottAccountId,
                 String ottAccountPassword, PartyStatus partyStatus, User leader) {
        this.ott = ott;
        this.ottAccountId = ottAccountId;
        this.ottAccountPassword = ottAccountPassword;
        this.partyStatus = partyStatus;
        this.leader = leader;
        this.settlementDate = getCreatedAt().plusMonths(1);
    }

    public void updatePartyOttAccount(PartyUpdateServiceRequest request) {
        ottAccountId = request.ottAccountId();
        ottAccountPassword = request.ottAccountPassword();
    }

    public void changeStatus(int capacity) {
        if (capacity == partyMembers.size()) {
            this.partyStatus = PartyStatus.FULL;
        }
    }

    public void plusSettlement() {
        settlementDate = getSettlementDate().plusMonths(1);
    }

}
