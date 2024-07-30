package com.elice.nbbang.domain.party.service.schedule;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.service.PartyMatchService;
import com.elice.nbbang.global.util.TaskManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyMatchingScheduler {
    private final OttRepository ottRepository;
    private final PartyMatchService partyMatchService;
    private final TaskManager<Long> taskManager;
    private final RedisTemplate<String, Long> redisTemplate;


//    @Scheduled(fixedRate = 5000)
    public void schedulePartyMatching() {
        List<Ott> otts = ottRepository.findAll();
        for (Ott ott : otts) {
            if (taskManager.addTask(ott.getId())) {
                try {
                    List<Long> waitingUsers = redisTemplate.opsForList().range("waiting:" + ott.getId(), 0, -1);
                    for (Long userId : waitingUsers) {
                        partyMatchService.partyMatch(userId, ott.getId());
                        redisTemplate.opsForList().remove("waiting:" + ott.getId(), 1, userId);

                    }
                } finally {
                    taskManager.removeTask(ott.getId());

                }
            }
        }
    }
}
