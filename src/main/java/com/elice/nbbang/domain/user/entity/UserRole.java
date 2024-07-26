package com.elice.nbbang.domain.user.entity;

public enum UserRole {
    USER,
    ADMIN;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
