package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.auth.dto.TokenRefreshRequest;
import com.elice.nbbang.domain.user.dto.CustomUserDetails;
import com.elice.nbbang.domain.user.dto.UserLogInDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/user-login")
    public ResponseEntity<?> login(@RequestBody UserLogInDto userLogInDto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogInDto.getEmail(), userLogInDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userService.findByEmail(userLogInDto.getEmail());

            String accessToken = jwtUtil.createJwt("category", user.getEmail(), user.getRole().name(), 3600000L);
            String refreshToken = jwtUtil.createJwt("category", user.getEmail(), user.getRole().name(), 7200000L);

            // Set refresh token in cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);

            // Return access token in response header
            response.setHeader("Authorization", "Bearer " + accessToken);

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            logger.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request, HttpServletResponse response) {
        String requestRefreshToken = request.getRefreshToken();
        String email = jwtUtil.getEmail(requestRefreshToken);

        if (jwtUtil.isExpired(requestRefreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Refresh Token");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.createJwt("category", userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 3600000L);

        // Return new access token in response header
        response.setHeader("Authorization", "Bearer " + newAccessToken);

        return ResponseEntity.ok("Token refreshed");
    }
}