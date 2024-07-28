package com.elice.nbbang.domain.user.controller;


import com.elice.nbbang.domain.auth.dto.AuthResponse;
import com.elice.nbbang.domain.auth.dto.TokenRefreshRequest;
import com.elice.nbbang.domain.user.dto.CustomUserDetails;
import com.elice.nbbang.domain.user.dto.UserLogInDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@RestController
//@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @GetMapping("/")
    public String userP() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "User Controller" + email + role;
    }

    @PostMapping("/users/user-login")
    public ResponseEntity<?> login(@RequestBody UserLogInDto userLogInDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogInDto.getEmail(), userLogInDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.createJwt("category", userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 3600000L);
        String refreshToken = jwtUtil.createJwt("category", userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 7200000L); // 리프레시 토큰은 더 긴 유효기간을 가집니다.

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
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

        return ResponseEntity.ok(new AuthResponse(newAccessToken, requestRefreshToken));
    }

}