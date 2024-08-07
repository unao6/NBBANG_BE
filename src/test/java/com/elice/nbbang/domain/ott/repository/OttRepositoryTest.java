package com.elice.nbbang.domain.ott.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
@SpringBootTest
class OttRepositoryTest {

    @Autowired
    private OttRepository ottRepository;

    @Autowired
    private PartyRepository partyRepository;
    @AfterEach
    void tearDown() {
        partyRepository.deleteAllInBatch();
        ottRepository.deleteAllInBatch();
    }

    @DisplayName("중복된 이름을 가진 OTT 가 있는지 체크한다.")
    @Test
    void checkDuplicateOttName() throws Exception {
        //given
        Ott ott1 = Ott.of("ChatGPT", 40000, 5);
        Ott ott2 = Ott.of("Disney+", 20000, 3);
        ottRepository.saveAll(List.of(ott1, ott2));

        //when
        Boolean isExist = ottRepository.existsByName("ChatGPT");

        //then
        assertThat(isExist).isTrue();
    }

    @DisplayName("중복된 이름을 가진 OTT 가 있는지 체크한다.")
    @Test
    void checkDuplicateOttName2() throws Exception {
        //given
        Ott ott1 = Ott.of("ChatGPT", 40000, 5);
        Ott ott2 = Ott.of("Disney+", 20000, 3);
        ottRepository.saveAll(List.of(ott1, ott2));

        //when
        Boolean isExist = ottRepository.existsByName("Netflix");

        //then
        assertThat(isExist).isFalse();
    }


}