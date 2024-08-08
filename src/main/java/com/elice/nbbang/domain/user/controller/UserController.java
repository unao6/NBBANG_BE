package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.dto.request.ChangePhoneNumberRequest;
import com.elice.nbbang.domain.user.dto.reponse.UserResponse;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserUtil userUtil;

    @GetMapping("/user-info")
    public ResponseEntity<UserResponse> getUserInfo() {
        return ResponseEntity.ok().body(userService.getUserInfo());
    }

    // 휴대폰 번호 변경
    @PutMapping("/change-phone-number")
    public ResponseEntity<?> changePhoneNumber(@RequestBody ChangePhoneNumberRequest request) {
        String email = userUtil.getAuthenticatedUserEmail();
        System.out.println("Authenticated user email: " + email);
        System.out.println("New phone number: " + request.getNewPhoneNumber());
        System.out.println("Verification code: " + request.getRandomNumber());
        boolean isChanged = userService.changePhoneNumber(email, request.getNewPhoneNumber(), request.getRandomNumber());
        if (isChanged) {
            return ResponseEntity.ok("휴대폰 번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("휴대폰 번호 변경에 실패했습니다.");
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/delete-account/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}