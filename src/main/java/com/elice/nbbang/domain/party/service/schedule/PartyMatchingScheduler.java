package com.elice.nbbang.domain.party.service.schedule;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.MatchingType;
import com.elice.nbbang.domain.party.service.PartyMatchService;
import com.elice.nbbang.domain.party.service.dto.PartyMatchServiceRequest;
import com.elice.nbbang.global.util.TaskManager;
import java.util.ArrayList;
import java.util.Collections;
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

    @Scheduled(fixedRate = 5000)
    public void schedulePartyMatching() {
        List<Ott> otts = ottRepository.findAll();
        for (Ott ott : otts) {
            if (taskManager.addTask(ott.getId())) {
                try {
                    List<String> waitingUsers = redisTemplate.opsForList().range("waiting:" + ott.getId(), 0, -1);
                    if (waitingUsers == null) {
                        waitingUsers = new ArrayList<>();
                    }
                    for (String userRequest : waitingUsers) {
                        PartyMatchServiceRequest request = deserializeRequest(userRequest);
                        CompletableFuture<Boolean> matchingResult = partyMatchService.partyMatch(request.userId(), request.type(), ott.getId());
                        try {
                            if (matchingResult.get()) {
                                redisTemplate.opsForList().remove("waiting:" + ott.getId(), 1, userRequest);
                            }
                        } catch (Exception e) {
                            log.error("파티 매칭 비동기 메서드 호출시 에러", e);
                        }
                    }
                } finally {
                    taskManager.removeTask(ott.getId());
                }
            }
        }
    }

    private PartyMatchServiceRequest deserializeRequest(String requestString) {
        String[] parts = requestString.split(",");
        Long userId = Long.parseLong(parts[0]);
        MatchingType type = MatchingType.valueOf(parts[1]);
        Long ottId = Long.parseLong(parts[2]);
        return new PartyMatchServiceRequest(userId, type, ottId);
    }
}
