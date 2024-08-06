package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.AccountRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Account;
import com.elice.nbbang.domain.payment.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/account")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity<Account> getAccount() {
        Account account = accountService.getAccount();
        return ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestBody AccountRegisterDTO dto) {
        Account account = accountService.registerAccount(dto);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount() {
        accountService.deleteAccount();
        return ResponseEntity.ok().build();
    }
}
