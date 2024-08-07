package com.elice.nbbang.domain.ott.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.elice.nbbang.domain.ott.controller.dto.OttCreateRequest;
import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.ott.controller.dto.OttUpdateRequest;
import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.DuplicateOttName;
import com.elice.nbbang.domain.ott.exception.InvalidOttCapacity;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
@SpringBootTest
class OttServiceTest {

    @Autowired
    private OttRepository ottRepository;
    @Autowired
    private OttService ottService;

    @AfterEach
    void tearDown() {
        ottRepository.deleteAllInBatch();
    }

    @DisplayName("OTT 정보를 받아 OTT를 생성한다.")
    @Test
    void createOtt() throws Exception {
        //given
        OttCreateRequest request = new OttCreateRequest("ChatGPT", 40000, 5);

        //when
        Long ott = ottService.createOtt(request.toServiceRequest());

        //then
        assertThat(ott).isNotNull();
    }

    @DisplayName("중복된 이름의 OTT 를 생성하려는 경우 예외가 발생한다.")
    @Test
    void createOttByDuplicateName() throws Exception {
        //given
        Ott ott = Ott.of("ChatGPT", 40000, 5);
        ottRepository.save(ott);
        OttCreateRequest request = new OttCreateRequest("ChatGPT", 40000, 5);

        //when //then
        assertThatThrownBy(() -> ottService.createOtt(request.toServiceRequest()))
                .isInstanceOf(DuplicateOttName.class)
                .hasMessage("중복된 OTT 서비스 입니다.");
    }

    @DisplayName("OTT 파티의 인원수는 1~5 범위만 허용한다.")
    @Test
    void createOttByInvalidCapacity() throws Exception {
        //given
        OttCreateRequest request = new OttCreateRequest("ChatGPT", 40000, 6);

        //when //then
        assertThatThrownBy(() -> ottService.createOtt(request.toServiceRequest()))
                .isInstanceOf(InvalidOttCapacity.class)
                .hasMessage("잘못된 인원수 입니다.");

    }
    @DisplayName("OTT 파티의 인원수는 1~5 범위만 허용한다.")
    @Test
    void createOttByInvalidCapacity2() throws Exception {
        //given
        OttCreateRequest request = new OttCreateRequest("ChatGPT", 40000, 0);

        //when //then
        assertThatThrownBy(() -> ottService.createOtt(request.toServiceRequest()))
                .isInstanceOf(InvalidOttCapacity.class)
                .hasMessage("잘못된 인원수 입니다.");

    }

    @DisplayName("등록된 모든 OTT 서비스의 정보를 조회한다.")
    @Test
    void getAllOtt() throws Exception {
        //given
        Ott ott1 = Ott.of("Netflix", 20000, 4);
        Ott ott2 = Ott.of("TVING", 10000, 3);

        ottRepository.saveAll(List.of(ott1, ott2));

        //when
        List<OttResponse> allOtt = ottService.getAllOtt();

        //then
        assertThat(allOtt).hasSize(2);
        assertThat(allOtt)
                .extracting("name", "price", "capacity")
                .containsExactlyInAnyOrder(
                        tuple("Netflix", 20000, 4),
                        tuple("TVING", 10000, 3)
                );
    }

    @DisplayName("OTT id 정보를 이용해서 단건 조회할 수 있다.")
    @Test
    void getOttById() throws Exception {
        //given
        Ott ott1 = Ott.of("Netflix", 20000, 4);
        Ott ott2 = Ott.of("TVING", 10000, 3);

        ottRepository.saveAll(List.of(ott1, ott2));

        //when
        OttResponse ottById = ottService.getOttById(ott1.getId());

        //then
        assertThat(ottById)
                .extracting("name", "price", "capacity")
                .contains("Netflix", 20000, 4);
    }
    @DisplayName("OTT 정보를 수정할 수 있다.")
    @Test
    void updateOtt() throws Exception {
        //given
        Ott ott1 = Ott.of("Netflix", 20000, 4);
        ottRepository.save(ott1);

        OttUpdateRequest request = new OttUpdateRequest(ott1.getId(), "Netflix", 30000, 4);

        //when
        ottService.updateOtt(request.toServiceRequest());
        Ott updatedOtt = ottRepository.findById(ott1.getId())
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        //then
        assertThat(updatedOtt)
                .extracting("name", "price", "capacity")
                .contains("Netflix", 30000, 4);
    }

    @DisplayName("OTT 정보를 삭제할 수 있다.")
    @Test
    void deleteOtt() throws Exception {
        //given
        Ott ott1 = Ott.of("Netflix", 20000, 4);
        ottRepository.save(ott1);

        //when
        ottService.deleteOtt(ott1.getId());

        //then
        assertFalse(ottRepository.findById(ott1.getId()).isPresent());
    }

}