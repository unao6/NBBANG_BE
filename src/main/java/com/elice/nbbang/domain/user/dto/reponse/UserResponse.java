package com.elice.nbbang.domain.user.dto.reponse;

import com.elice.nbbang.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

        private Long userId;
        private String email;
        private String nickname;
        private UserRole role;
        private String phoneNumber;
        private boolean isAdmin;

}
