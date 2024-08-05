package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/admin")
    public String adminP(){
        return "Admin Controller";
    }

    // 회원 탈퇴
    @DeleteMapping("/delete-account/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 회원 복구
    @PutMapping("/restore-account/{email}")
    public ResponseEntity<String> restoreUser(@PathVariable String email) {
        userService.restoreUser(email);
        return ResponseEntity.ok("계정이 복구되었습니다.");
    }
}
