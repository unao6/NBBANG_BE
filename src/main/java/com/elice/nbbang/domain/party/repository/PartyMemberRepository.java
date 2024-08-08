package com.elice.nbbang.domain.party.repository;

import com.elice.nbbang.domain.party.entity.PartyMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {

    @Query("select pm "
            + "from PartyMember pm"
            + " join fetch pm.party p"
            + " join fetch pm.user"
            + " where p.id =:partyId")
    List<PartyMember> findByPartyIdWithPartyAndUser(@Param("partyId") Long partyId);

    @Query("select pm "
            + "from PartyMember pm "
            + "join fetch pm.ott o "
            + "join fetch pm.user u "
            + "where o.id =:ottId "
            + "and u.id =:userId")
    PartyMember findPartyMemberByOttIdAndUserId(@Param("ottId") Long ottId, @Param("userId") Long userId);


    @Query("select pm from PartyMember pm join fetch pm.party where pm.party.id = :partyId")
    List<PartyMember> findPartyMemberByPartyId(Long partyId);

}

