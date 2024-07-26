package com.elice.nbbang.domain.party.repository;

import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    Optional<PartyMember> findByUser(User user);
}

