package com.elice.nbbang.global.util;

import com.elice.nbbang.domain.user.dto.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private static final Logger logger = LoggerFactory.getLogger(UserUtil.class);

    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                logger.info("Authenticated user email: {}", userDetails.getUsername());
                return userDetails.getUsername(); // 여기서 반환되는 값이 이메일이어야 함
            }
        }

        logger.warn("No authenticated user found or authentication is not valid");
        return null;
    }
}