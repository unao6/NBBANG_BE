package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.payment.dto.AccountInfoResponse;
import com.elice.nbbang.domain.payment.dto.AccountRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Account;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.AccountType;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.AccountRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserUtilService;
import com.elice.nbbang.global.config.EncryptUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserUtilService userUtilService;
    private final PartyRepository partyRepository;
    private final PaymentRepository paymentRepository;
    private final EncryptUtils encryptUtils;

    //계좌 조회
    public AccountInfoResponse getAccount() {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));

        String decryptedAccountNumber = encryptUtils.decrypt(account.getAccountNumber());
        AccountInfoResponse accountInfoResponse = AccountInfoResponse.builder()
            .bankName(account.getBankName())
            .accountNumber(decryptedAccountNumber)
            .build();
        return accountInfoResponse;
    }

    //유저 계좌 등록
    @Transactional(readOnly = false)
    public AccountInfoResponse registerAccount(AccountRegisterDTO dto) {
        User user = userUtilService.getUserByEmail();

        Account existingAccount = accountRepository.findByUserId(user.getId()).orElse(null);
        if (existingAccount != null) {
            accountRepository.delete(existingAccount);
            accountRepository.flush();
        }

        String encryptedAccountNumber = encryptUtils.encrypt(dto.getAccountNumber());
        Account account = Account.builder()
            .user(user)
            .accountNumber(encryptedAccountNumber)
            .bankName(dto.getBankName())
            .balance(0L)
            .build();
        accountRepository.save(account);

        AccountInfoResponse accountInfoResponse = AccountInfoResponse.builder()
            .accountNumber(dto.getAccountNumber())
            .bankName(account.getBankName())
            .build();
        return accountInfoResponse;
    }

    //계좌 삭제
    @Transactional(readOnly = false)
    public void deleteAccount() {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));
        accountRepository.delete(account);
    }

    //파티를 주기마다 조회
    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void scheduledLookupParty() {
        List<Party> partyList = partyRepository.findBySettlementDateBefore(LocalDateTime.now());

        for (Party party : partyList) {
            caculateAccount(party);
        }
    }

    //파티장 정산
    @Transactional(readOnly = false)
    public void caculateAccount(Party party) {
        Account serviceAccount = accountRepository.findByAccountType(AccountType.SERVICE_ACCOUNT)
            .orElseThrow(() -> new IllegalArgumentException("서비스 계좌가 존재하지 않습니다."));

        int amount = (party.getOtt().getPrice() / party.getOtt().getCapacity() * (party.getOtt().getCapacity() - 1)) - PaymentService.SETTLEMENT_FEE;
        Long leaderId = party.getLeader().getId();

        Account userAccount = accountRepository.findByUserId(leaderId)
            .orElseThrow(() -> new IllegalArgumentException("유저 계좌가 존재하지 않습니다."));

        //서비스 계좌의 잔액 -, 유저 계좌의 잔액 +, 파티의 정산일 +1달
        serviceAccount.decreaseBalance((long) amount);
        userAccount.increaseBalance((long) amount);
        party.plusSettlement();

        //Payment 추가
        Payment payment = Payment.builder()
            .amount(amount)
            .ottId(party.getOtt().getId())
            .user(party.getLeader())
            .bankName(userAccount.getBankName())
            .paymentType(PaymentType.SETTLEMENT)
            .status(PaymentStatus.SETTLE_COMPLETED)
            .build();
        paymentRepository.save(payment);
    }

    //파티장 부분 정산
    @Transactional(readOnly = false)
    public void calculatePartialSettlement(Party party) {
        int amount = (party.getOtt().getPrice() / party.getOtt().getCapacity() * (party.getOtt().getCapacity() - 1)) - PaymentService.SETTLEMENT_FEE;
        long daysUntilSettlement = ChronoUnit.DAYS.between(LocalDateTime.now(), party.getSettlementDate());
        long totalSettlement = ChronoUnit.DAYS.between(party.getSettlementDate().minusMonths(1), party.getSettlementDate());

        double ratio = (double) daysUntilSettlement / totalSettlement;
        long partialAmount = (long) Math.max((amount * ratio), 0);

        Account serviceAccount = accountRepository.findByAccountType(AccountType.SERVICE_ACCOUNT)
            .orElseThrow(() -> new IllegalArgumentException("서비스 계좌가 존재하지 않습니다."));
        Account userAccount = accountRepository.findByUserId(party.getLeader().getId())
            .orElseThrow(() -> new IllegalArgumentException("유저 계좌가 존재하지 않습니다."));

        if (partialAmount > 0) {
            serviceAccount.decreaseBalance(partialAmount);
            userAccount.increaseBalance(partialAmount);
        }

        //Payment 추가
        Payment payment = Payment.builder()
            .amount((int) partialAmount)
            .ottId(party.getOtt().getId())
            .user(party.getLeader())
            .bankName(userAccount.getBankName())
            .paymentType(PaymentType.SETTLEMENT)
            .status(PaymentStatus.PARTIAL_SETTLEMENT_COMPLETED)
            .build();
        paymentRepository.save(payment);
    }
}
