//package com.elice.nbbang.domain.party.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.elice.nbbang.domain.ott.entity.Ott;
//import com.elice.nbbang.domain.ott.repository.OttRepository;
//import com.elice.nbbang.domain.party.dto.PartyCreateRequest;
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class PartyServiceTest {
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
//    @DisplayName("자신의 OTT 계정을 공유해 파티장으로 파티를 생성할 수 있다.")
//    @Test
//    void createParty() throws Exception {
//        //given
//        Ott ott = Ott.of("ChatGPT", 40000, 5);
//        User user = new User("홍길동", "1234");
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
//        Long party = partyService.createParty(request);
//
//        //then
//        assertThat(party).isNotNull();
//    }
//
//}