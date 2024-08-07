package com.elice.nbbang.domain.ott.repository;

import com.elice.nbbang.domain.ott.entity.Ott;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OttRepository extends JpaRepository<Ott, Long> {
    Boolean existsByName(String name);
}
