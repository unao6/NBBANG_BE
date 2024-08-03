//package com.elice.nbbang.domain.party.service;
//
//import static com.elice.nbbang.domain.user.entity.UserRole.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.elice.nbbang.domain.ott.entity.Ott;
//import com.elice.nbbang.domain.ott.repository.OttRepository;
//import com.elice.nbbang.domain.party.controller.dto.PartyMatchRequest;
//import com.elice.nbbang.domain.party.entity.Party;
//import com.elice.nbbang.domain.party.entity.PartyStatus;
//import com.elice.nbbang.domain.party.repository.PartyRepository;
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.entity.UserRole;
//import com.elice.nbbang.domain.user.repository.UserRepository;
//import java.util.List;
//import java.util.Set;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.TestPropertySource;
//
//@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
//@SpringBootTest
//class PartyMatchServiceTest {
//
//    @Autowired
//    private PartyMatchService partyMatchService;
//
//    @Autowired
//    private PartyRepository partyRepository;
//
//    @Autowired
//    private OttRepository ottRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RedisTemplate<String, Long> redisTemplate;
//
//    @AfterEach
//    void tearDown() {
//        // Redis의 모든 키 삭제
//        Set<String> keys = redisTemplate.keys("*");
//        if (keys != null && !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//        }
//        ottRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
//    @DisplayName("자동 매칭을 신청하고 매칭 대기 큐에 들어간다.")
//    @Test
//    void addPartyMatchingQueueInRedis() throws Exception {
//        //given
//        Ott ott = Ott.of("ChatGpt", 20000, 5);
//        ottRepository.save(ott);
//
//        User user = User.builder()
//                .email("test@email.com")
//                .password("password!")
//                .nickname("test")
//                .phoneNumber("010-1234-5678")
//                .role(UserRole.ROLE_USER)
//                .build();
//        userRepository.save(user);
//
//        PartyMatchRequest request = new PartyMatchRequest(user.getId(), ott.getId());
//
//        //when
//        boolean result = partyMatchService.addPartyMatchingQueue(request.toServiceRequest());
//
//        //then
//
//        ListOperations<String, Long> listOps = redisTemplate.opsForList();
//        Long userIdInQueue = listOps.rightPop("waiting:" + ott.getId());
//
//        assertThat(result).isTrue();
//        assertThat(userIdInQueue).isEqualTo(user.getId());
//    }
//
//    @DisplayName("조건에 맞는 파티를 찾아 매칭해준다.")
//    @Test
//    void partyMatch() throws Exception {
//        //given
//        User user = createUser("testUser@naver.com", "010-1234-5678", "test", "test1234");
//        User user1 = createUser("testUser1@naver.com", "010-1254-5678", "test1", "test12345");
//        User user2 = createUser("testUser2@naver.com", "010-1264-5678", "test2", "test12346");
//        userRepository.saveAll(List.of(user, user1, user2));
//
//        Ott ott = createOtt("ChatGpt", 20000, 5);
//        Ott ott1 = createOtt("Netflix", 25000, 4);
//        Ott ott2 = createOtt("TVING", 10000, 3);
//        ottRepository.saveAll(List.of(ott, ott1, ott2));
//
//        Party party = createParty(ott, user);
//        Party party1 = createParty(ott1, user1);
//        Party party2 = createParty(ott2, user2);
//        partyRepository.saveAll(List.of(party, party1, party2));
//        //when
//
//        //then
//    }
//
//    private Party createParty(Ott ott, User user) {
//        return Party.builder()
//                .ottAccountId("test@naver.com")
//                .ottAccountPassword("test1234")
//                .ott(ott)
//                .leader(user)
//                .partyStatus(PartyStatus.AVAILABLE)
//                .build();
//    }
//
//    private Ott createOtt(String name, int price, int capacity) {
//        return Ott.of(name, price, capacity);
//    }
//
//    private User createUser(String mail, String phoneNumber, String test, String password) {
//        return User.builder()
//                .email(mail)
//                .nickname(test)
//                .phoneNumber(phoneNumber)
//                .password(password)
//                .role(USER)
//                .build();
//    }
//
//
//
//
//
//}