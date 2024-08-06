package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.auth.dto.TokenRefreshRequest;
import com.elice.nbbang.domain.user.dto.CustomUserDetails;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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


    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        Long id;
        if (principal instanceof CustomUserDetails) {
            email = ((CustomUserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }
}
//    @PostMapping("/refresh-token")
//    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request, HttpServletResponse response) {
//        String requestRefreshToken = request.getRefreshToken();
//
//        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is missing or empty");
//        }
//
//        String email;
//        try {
//            email = jwtUtil.getEmail(requestRefreshToken);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Refresh Token");
//        }
//
//        if (jwtUtil.isExpired(requestRefreshToken)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired Refresh Token");
//        }
//
//        User user = userService.findByEmail(email);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
//        }
//
//        CustomUserDetails userDetails = new CustomUserDetails(user);
//        String newAccessToken = jwtUtil.createJwt("category", userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 3600000L);
//
//        // Return new access token in response header
//        response.setHeader("access", newAccessToken);
//
//        return ResponseEntity.ok("Token refreshed");
//    }
