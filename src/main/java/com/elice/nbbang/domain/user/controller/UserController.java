package com.elice.nbbang.domain.user.controller;


import com.elice.nbbang.domain.user.dto.CustomUserDetails;
import com.elice.nbbang.domain.user.dto.UserLogInDto;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

@RestController
//@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

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

//    @PostMapping("/api/users/user-login")
//    public ResponseEntity<?> login(@RequestBody UserLogInDto userLogInDto) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(userLogInDto.getEmail(), userLogInDto.getPassword())
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        String jwt = jwtUtil.createJwt(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 3600000L);
//
//        return ResponseEntity.ok().header("Authorization", "Bearer " + jwt).body("Login successful");
//    }
//
}