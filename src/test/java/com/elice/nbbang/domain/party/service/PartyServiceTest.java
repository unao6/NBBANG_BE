//package com.elice.nbbang.domain.party.service;
//
//import static com.elice.nbbang.domain.user.entity.UserRole.USER;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.elice.nbbang.domain.ott.entity.Ott;
//import com.elice.nbbang.domain.ott.repository.OttRepository;
//import com.elice.nbbang.domain.party.controller.dto.PartyCreateRequest;
//import com.elice.nbbang.domain.party.repository.PartyRepository;
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.entity.UserRole;
//import com.elice.nbbang.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
//@SpringBootTest
//class PartyServiceTest {
//
//
//    @Autowired
//    private PartyRepository partyRepository;
//
//    @Autowired
//    private PartyService partyService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OttRepository ottRepository;
//
//    @AfterEach
//    void tearDown() {
//        partyRepository.deleteAllInBatch();
//        ottRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
//
//    @DisplayName("자신의 OTT 계정을 공유해 파티장으로 파티를 생성할 수 있다.")
//    @Test
//    void createParty() throws Exception {
//        //given
//        Ott ott = Ott.of("ChatGPT", 40000, 5);
//        User user = User.builder()
//                .email("mail@naver.com")
//                .nickname("test")
//                .phoneNumber("010-1234-5678")
//                .password("password")
//                .role(UserRole.ROLE_USER)
//                .build();
//
//        ottRepository.save(ott);
//        userRepository.save(user);
//
//        PartyCreateRequest request = new PartyCreateRequest(
//                ott.getId(),
//                user.getId(),
//                "opix0306@naver.com",
//                "qwer1234"
//        );
//
//        //when
//        Long party = partyService.createParty(request.toServiceRequest());
//
//        //then
//        assertThat(party).isNotNull();
//    }
//
//}