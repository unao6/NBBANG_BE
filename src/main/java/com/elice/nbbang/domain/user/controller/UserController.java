package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.dto.UserResponse;
import com.elice.nbbang.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user-info")
    public ResponseEntity<UserResponse> getUserInfo() {
        return ResponseEntity.ok().body(userService.getUserInfo());
    }
}