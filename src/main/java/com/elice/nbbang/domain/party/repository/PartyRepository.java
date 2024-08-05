package com.elice.nbbang.domain.party.repository;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.party.entity.PartyStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyRepository extends JpaRepository<Party, Long> {

    Optional<Party> findByLeaderIdAndOttId(Long leaderId, Long ottId);


    @Query("select p "
            + "from Party p "
            + "join fetch p.ott o "
            + "where p.ott.id = :ottId "
            + "and p.partyStatus = :partyStatus")
    List<Party> findAvailablePartyByOtt(@Param("ottId") Long ottId, @Param("partyStatus") PartyStatus partyStatus);

    @Query("select distinct p" +
            " from Party p" +
            " left join PartyMember pm on p = pm.party" +
            " where pm.user.id = :userId" +
            " or p.leader.id = :userId")
    List<Party> findSubscribedOttByUserId(@Param("userId") Long userId);

    @Query("select p" +
            " from Party p" +
            " join fetch p.leader u" +
            " where p.id =:partyId" +
            " and p.leader.id = :userId ")
    Optional<Party> findByPartyIdAndUserId(@Param("partyId") Long partyId, @Param("userId") Long userId);

    @Query("select p" +
            " from Party p" +
            " join fetch p.leader" +
            " join fetch p.ott" +
            " where p.id = :partyId")
    Party findPartyAndPartyMemberByPartyId(@Param("partyId") Long partyId);

}
