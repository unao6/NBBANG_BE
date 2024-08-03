package com.elice.nbbang.domain.user.repository;

import com.elice.nbbang.domain.user.entity.MailCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailCertification, Long> {

    MailCertification findByEmail(String email);
}
