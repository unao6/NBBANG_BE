package com.elice.nbbang.domain.party.repository;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartyRepository extends JpaRepository<Party, Long> {


    @Query("select p "
            + "from Party p "
            + "join fetch p.ott o "
            + "where p.ott.id = :ottId "
            + "and p.partyStatus = :partyStatus")
    List<Party> findAvailablePartyByOtt(@Param("ottId") Long ottId, @Param("partyStatus") PartyStatus partyStatus);

}
