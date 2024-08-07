package com.elice.nbbang.domain.user.dto;

import com.elice.nbbang.domain.user.entity.UserRole;

public record UserResponse(
        Long userId,
        String email,
        String nickname,
        UserRole role
) {
}
