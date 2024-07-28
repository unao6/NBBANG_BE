package com.elice.nbbang.domain.party.service;

import static com.elice.nbbang.domain.user.entity.UserRole.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.controller.dto.PartyMatchRequest;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "JASYPT_PASSWORD=1xladkagh")
@SpringBootTest
class PartyMatchServiceTest {

    @Autowired
    private PartyMatchService partyMatchService;

    @Autowired
    private OttRepository ottRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @AfterEach
    void tearDown() {
        // Redis의 모든 키 삭제
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        ottRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
    @DisplayName("자동 매칭을 신청하고 매칭 대기 큐에 들어간다.")
    @Test
    void addPartyMatchingQueueInRedis() throws Exception {
        //given
        Ott ott = Ott.of("ChatGpt", 20000, 5);
        ottRepository.save(ott);

        User user = User.builder()
                .email("test@email.com")
                .password("password!")
                .nickname("test")
                .phoneNumber("010-1234-5678")
                .role(USER)
                .build();
        userRepository.save(user);

        PartyMatchRequest request = new PartyMatchRequest(user.getId(), ott.getId());

        //when
        boolean result = partyMatchService.addPartyMatchingQueue(request.toServiceRequest());

        //then

        ListOperations<String, Long> listOps = redisTemplate.opsForList();
        Long userIdInQueue = listOps.rightPop("waiting:" + ott.getId());

        assertThat(result).isTrue();
        assertThat(userIdInQueue).isEqualTo(user.getId());
    }


}