package com.elice.nbbang.domain.user.controller;

//import com.elice.nbbang.domain.user.dto.UserSignUpDto;
//import com.elice.nbbang.domain.user.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
//@RestController
//@RequestMapping("/users")
//@RequiredArgsConstructor
public class UserController {

    @GetMapping("/")
    public String mainP() {
        return "User Controller";
    }

//    private final UserService userService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<String> signUp(@RequestBody UserSignUpDto userSignUpDto) {
//        userService.signUp(userSignUpDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully");
//    }

}