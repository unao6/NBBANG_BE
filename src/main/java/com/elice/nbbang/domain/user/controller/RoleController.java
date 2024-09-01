package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import kotlinx.serialization.Required;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elice.nbbang.global.util.UserUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    @GetMapping("/check")
    public boolean roleCheck() {
        String email = userUtil.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email);
        if(user.getRole() == UserRole.ROLE_ADMIN){
            return true;
        }
        return false;
    }
}
