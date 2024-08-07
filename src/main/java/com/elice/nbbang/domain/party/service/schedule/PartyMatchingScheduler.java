package com.elice.nbbang.domain.party.service.schedule;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.MatchingType;
import com.elice.nbbang.domain.party.service.PartyMatchService;
import com.elice.nbbang.domain.party.service.dto.PartyMatchServiceRequest;
import com.elice.nbbang.global.util.TaskManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyMatchingScheduler {
    private final OttRepository ottRepository;
    private final PartyMatchService partyMatchService;
    private final TaskManager<Long> taskManager;
    private final RedisTemplate<String, String> redisTemplate;


//    @Scheduled(fixedRate = 5000)
    public void schedulePartyMatching() throws Exception {
        List<Ott> otts = ottRepository.findAll();
        for (Ott ott : otts) {
            if (taskManager.addTask(ott.getId())) {
                try {
                    List<String> waitingUsers = redisTemplate.opsForList().range("waiting:" + ott.getId(), 0, -1);

                    // waitingUsers가 null이 아니고 비어있지 않을 때만 매칭 로직을 실행
                    if (waitingUsers != null && !waitingUsers.isEmpty()) {
                        log.info("waitingUsers null 아님");
                        for (String userRequest : waitingUsers) {
                            String[] parts = userRequest.split(",");
                            Long userId = Long.parseLong(parts[0]);
                            MatchingType type = MatchingType.valueOf(parts[1]);

                            CompletableFuture<Boolean> matchingResult = partyMatchService.partyMatch(userId, type, ott.getId());
                            try {
                                if (matchingResult.get()) {
                                    log.info("매칭 성공");
                                    redisTemplate.opsForList().remove("waiting:" + ott.getId(), 1, userRequest);
                                    String duplicateValue = userId + "," + ott.getId();
                                    redisTemplate.opsForSet().remove("waiting_set:" + ott.getId(), duplicateValue);
                                }
                            } catch (Exception e) {
                                log.error("파티 매칭 비동기 메서드 호출시 에러", e);
                            }
                        }
                    } else {
                        log.info("매칭 대기 중인 유저가 없습니다.");
                    }
                } finally {
                    taskManager.removeTask(ott.getId());
                }
            }
        }
    }

}
