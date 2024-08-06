//package com.elice.nbbang.global;
//
//import com.elice.nbbang.domain.ott.entity.Ott;
//import com.elice.nbbang.domain.ott.repository.OttRepository;
//import com.elice.nbbang.domain.party.entity.Party;
//import com.elice.nbbang.domain.party.entity.PartyStatus;
//import com.elice.nbbang.domain.party.repository.PartyRepository;
//import com.elice.nbbang.domain.party.entity.PartyMember;
//import com.elice.nbbang.domain.party.repository.PartyMemberRepository;
//import com.elice.nbbang.domain.payment.entity.Account;
//import com.elice.nbbang.domain.payment.entity.enums.AccountType;
//import com.elice.nbbang.domain.payment.repository.AccountRepository;
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.entity.UserRole;
//import com.elice.nbbang.domain.user.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//
//@Component
//public class DataLoader implements CommandLineRunner {
//    private final UserRepository userRepository;
//    private final OttRepository ottRepository;
//    private final PartyRepository partyRepository;
//    private final PartyMemberRepository partyMemberRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
//    private final AccountRepository accountRepository;
//
//    public DataLoader(UserRepository userRepository, OttRepository ottRepository, PartyRepository partyRepository,
//        PartyMemberRepository partyMemberRepository, BCryptPasswordEncoder passwordEncoder, AccountRepository accountRepository) {
//        this.userRepository = userRepository;
//        this.ottRepository = ottRepository;
//        this.partyRepository = partyRepository;
//        this.partyMemberRepository = partyMemberRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.accountRepository = accountRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 초기 OTT 데이터 설정
//        Ott disneyPlus = ottRepository.save(Ott.of("디즈니플러스", 9900, 4));
//        Ott chatGpt = ottRepository.save(Ott.of("ChatGpT", 29900, 4));
//        Ott tving = ottRepository.save(Ott.of("TVING", 9900, 4));
//
//        // 초기 사용자 데이터 설정
//        User user1 = userRepository.save(User.builder()
//            .nickname("JohnDoe")
//            .email("khp@naver.com")
//            .password(passwordEncoder.encode("1234"))
//            .phoneNumber("010-1234-5678")
//            .role(UserRole.ROLE_USER)
//            .build());
//
//        User user2 = userRepository.save(User.builder()
//            .nickname("JaneDoe")
//            .email("test2doe@example.com")
//            .password(passwordEncoder.encode("test2"))
//            .phoneNumber("010-2345-6789")
//            .role(UserRole.ROLE_USER)
//            .build());
//
//        User adminUser = userRepository.save(User.builder()
//            .nickname("AdminUser")
//            .email("admin@example.com")
//            .password(passwordEncoder.encode("admin"))
//            .phoneNumber("010-3456-7890")
//            .role(UserRole.ROLE_ADMIN)
//            .build());
//
//        // 초기 파티 데이터 설정
//        Party party1 = partyRepository.save(Party.builder()
//            .ott(disneyPlus)
//            .ottAccountId("netflix_user@example.com")
//            .ottAccountPassword("netflixpass")
//            .partyStatus(PartyStatus.AVAILABLE)
//            .leader(user1)
//            .build());
//
//        Party party2 = partyRepository.save(Party.builder()
//            .ott(chatGpt)
//            .ottAccountId("disney_user@example.com")
//            .ottAccountPassword("disneypass")
//            .partyStatus(PartyStatus.AVAILABLE)
//            .leader(user2)
//            .build());
//
//        Party party3 = partyRepository.save(Party.builder()
//            .ott(tving)
//            .ottAccountId("prime_user@example.com")
//            .ottAccountPassword("primepass")
//            .partyStatus(PartyStatus.AVAILABLE)
//            .leader(user2)
//            .build());
//
//        // 초기 파티 멤버 데이터 설정
//        partyMemberRepository.save(PartyMember.of(user1, party1, disneyPlus, LocalDateTime.of(2024, 8, 1, 0, 0)));
//        partyMemberRepository.save(PartyMember.of(user2, party1, disneyPlus, LocalDateTime.of(2024, 8, 1, 0, 0)));
//        partyMemberRepository.save(PartyMember.of(adminUser, party2, chatGpt, LocalDateTime.of(2024, 8, 1, 0, 0)));
//        partyMemberRepository.save(PartyMember.of(user1, party3, tving, LocalDateTime.of(2024, 8, 1, 0, 0)));
//
//        // 서비스계좌 생성
//        Account account = accountRepository.findByAccountType(AccountType.SERVICE_ACCOUNT).orElse(null);
//        if (account == null) {
//            Account serviceAccount = Account.builder()
//                .user(adminUser)
//                .accountNumber("1111")
//                .bankName("국민")
//                .accountType(AccountType.SERVICE_ACCOUNT)
//                .balance(500000000L)
//                .build();
//            accountRepository.save(serviceAccount);
//        }
//
//        // 초기 계좌 데이터 설정
//        Account account1 = accountRepository.save(
//            Account.builder()
//                .user(user1)
//                .accountNumber("1234")
//                .bankName("우리")
//                .balance(50000L)
//                .build()
//        );
//
//        Account account2 = accountRepository.save(
//            Account.builder()
//                .user(user2)
//                .accountNumber("5678")
//                .bankName("국민")
//                .balance(0L)
//                .build()
//        );
//    }
//}
