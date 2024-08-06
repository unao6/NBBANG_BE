package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.AccountRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Account;
import com.elice.nbbang.domain.payment.entity.enums.AccountType;
import com.elice.nbbang.domain.payment.repository.AccountRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserUtilService userUtilService;

    //계좌 조회
    public Account getAccount() {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));
        return account;
    }

    //유저 계좌 등록
    public Account registerAccount(AccountRegisterDTO dto) {
        User user = userUtilService.getUserByEmail();
        Account account = Account.builder()
            .user(user)
            .accountNumber(dto.getAccountNumber())
            .bankName(dto.getBankName())
            .build();
        accountRepository.save(account);
        return account;
    }

    //계좌 수정
    public Account updateAccount(AccountRegisterDTO dto) {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));
        Account updatedAccount = account.toBuilder()
            .accountNumber(dto.getAccountNumber())
            .bankName(dto.getBankName())
            .balance(0L)
            .build();
        accountRepository.save(updatedAccount);
        return updatedAccount;
    }

    //계좌 삭제
    public void deleteAccount() {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));
        accountRepository.delete(account);
    }

    //파티장 정산
    //TODO
    public void caculateAccount() {
        Account serviceAccount = accountRepository.findByAccountType(AccountType.SERVICE_ACCOUNT)
            .orElseThrow(() -> new IllegalArgumentException("서비스 계좌가 존재하지 않습니다."));


    }
}
