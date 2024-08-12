package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.AdminService;
import com.elice.nbbang.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/active")
    public Page<User> getActiveUsers(Pageable pageable) {
        return adminService.getAllActiveUsers(pageable);
    }

    @GetMapping("/inactive")
    public Page<User> getInactiveUsers(Pageable pageable) {
        return adminService.getAllInactiveUsers(pageable);
    }

    // 회원 복구
    @PutMapping("/restore-account/{email}")
    public ResponseEntity<String> restoreUser(@PathVariable String email) {
        adminService.restoreUser(email);

        return ResponseEntity.ok("계정이 복구되었습니다.");
    }

    // 사용자를 관리자 역할로 승격하는 엔드포인트
    @PutMapping("/make-admin/{email}")
    public ResponseEntity<String> makeAdmin(@PathVariable String email) {
        adminService.makeAdmin(email);

        return ResponseEntity.ok("User has been promoted to admin.");
    }

    // 사용자가 관리자 역할을 가지고 있는지 확인하는 엔드포인트
    @GetMapping("/is-admin/{email}")
    public ResponseEntity<Boolean> isAdmin(@PathVariable String email) {
        boolean isAdmin = adminService.isAdmin(email);

        return ResponseEntity.ok(isAdmin);
    }
}
