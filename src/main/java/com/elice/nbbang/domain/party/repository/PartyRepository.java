package com.elice.nbbang.domain.party.repository;

import com.elice.nbbang.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {

}
