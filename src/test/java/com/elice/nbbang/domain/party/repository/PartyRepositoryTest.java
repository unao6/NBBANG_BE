package com.elice.nbbang.domain.party.repository;

import static com.elice.nbbang.domain.user.entity.UserRole.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyStatus;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
@SpringBootTest
class PartyRepositoryTest {

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OttRepository ottRepository;

    @AfterEach
    void tearDown() {
        partyRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        ottRepository.deleteAllInBatch();
    }

    @DisplayName("파티 타입과 OTT 정보에 따른 매칭 가능한 파티들을 조회한다.")
    @Test
    void findPartyWithPartyStatusAndOtt() throws Exception {

        //given
        User user = createUser("testUser@naver.com", "010-1234-5678", "test", "test1234");
        User user1 = createUser("testUser1@naver.com", "010-1254-5678", "test1", "test12345");
        User user2 = createUser("testUser2@naver.com", "010-1264-5678", "test2", "test12346");
        userRepository.saveAll(List.of(user, user1, user2));

        Ott ott = createOtt("ChatGpt", 20000, 5);
        Ott ott1 = createOtt("Netflix", 25000, 4);
        Ott ott2 = createOtt("TVING", 10000, 3);
        ottRepository.saveAll(List.of(ott, ott1, ott2));

        Party party = createParty(ott, user);
        Party party1 = createParty(ott, user1);
        Party party2 = createParty(ott2, user2);
        partyRepository.saveAll(List.of(party, party1, party2));

        //when
        List<Party> availablePartyByOtt = partyRepository.findAvailablePartyByOtt(ott.getId(), PartyStatus.AVAILABLE);

        //then
        assertThat(availablePartyByOtt).hasSize(2);
    }



    private Party createParty(Ott ott, User user) {
        return Party.builder()
                .ottAccountId("test@naver.com")
                .ottAccountPassword("test1234")
                .ott(ott)
                .leader(user)
                .partyStatus(PartyStatus.AVAILABLE)
                .build();
    }

    private Ott createOtt(String name, int price, int capacity) {
        return Ott.of(name, price, capacity);
    }

    private User createUser(String mail, String phoneNumber, String test, String password) {
        return User.builder()
                .email(mail)
                .nickname(test)
                .phoneNumber(phoneNumber)
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();
    }

}