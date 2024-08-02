package com.elice.nbbang.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certification")
public class MailCertification {

    @Id
    private String email;
    private String certificationNumber;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean verified = false;
}
