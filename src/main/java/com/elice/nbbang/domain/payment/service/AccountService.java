package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.payment.dto.AccountRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Account;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.AccountType;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.AccountRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserUtilService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

    private static final Long SETTLEMENT_FEE = 200L;

    //계좌 조회
    public Account getAccount() {
        User user = userUtilService.getUserByEmail();
        Account account = accountRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 계좌가 없습니다."));
        return account;
    }

    //유저 계좌 등록
    @Transactional(readOnly = false)
    public Account registerAccount(AccountRegisterDTO dto) {
        User user = userUtilService.getUserByEmail();

        Account existingAccount = accountRepository.findByUserId(user.getId()).orElse(null);
        if (existingAccount != null) {
            accountRepository.delete(existingAccount);
            accountRepository.flush();
            Account account = Account.builder()
                .user(user)
                .accountNumber(dto.getAccountNumber())
                .bankName(dto.getBankName())
                .balance(0L)
                .build();
            accountRepository.save(account);
            return account;
        }

        Account account = Account.builder()
            .user(user)
            .accountNumber(dto.getAccountNumber())
            .bankName(dto.getBankName())
            .balance(0L)
            .build();
        accountRepository.save(account);
        return account;
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
    //@Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void scheduledLookupParty() {
        List<Party> partyList = partyRepository.findBySettlementDateBefore(LocalDateTime.now());

        List<Long> partyIds = partyList.stream()
            .map(Party::getId)
            .toList();

        for (Long id : partyIds) {
            caculateAccount(id);
        }
    }

    //파티장 정산
    @Transactional(readOnly = false)
    public void caculateAccount(Long id) {
        Account serviceAccount = accountRepository.findByAccountType(AccountType.SERVICE_ACCOUNT)
            .orElseThrow(() -> new IllegalArgumentException("서비스 계좌가 존재하지 않습니다."));

        Party party = partyRepository.findById(id).get();
        Long amount = (party.getOtt().getPrice() / party.getOtt().getCapacity() * (party.getOtt().getCapacity() - 1)) - SETTLEMENT_FEE;

        Long leaderId = party.getLeader().getId();

        Account userAccount = accountRepository.findByUserId(leaderId)
            .orElseThrow(() -> new IllegalArgumentException("유저 계좌가 존재하지 않습니다."));

        //서비스 계좌의 잔액 -, 유저 계좌의 잔액 +, 파티의 정산일 +1달
        serviceAccount.decreaseBalance(amount);
        userAccount.increaseBalance(amount);
        party.plusSettlement();

        //Payment 추가
        Payment payment = Payment.builder()
            .amount(amount.intValue())
            .ottId(party.getOtt().getId())
            .user(party.getLeader())
            .bankName(userAccount.getBankName())
            .status(PaymentStatus.SETTLE_COMPLETED)
            .build();
        paymentRepository.save(payment);
    }
}
