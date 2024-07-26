package com.elice.nbbang.domain.party.service;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.dto.PartyMatchRequest;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.party.entity.PartyStatus;
import com.elice.nbbang.domain.party.repository.PartyMemberRepository;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PartyMatchService {

    private final OttRepository ottRepository;
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    /*
    * 많은 수의 사용자가 동시에 자동 매칭을 시켯을 때 동시성 문제가 없나?
    * 있다면 처리를 어떻게 해야할까?
    * */
    public boolean addPartyMatchingQueue(final PartyMatchRequest request) {

        final Ott ott = ottRepository.findById(request.ottId())
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        // 커스텀 예외 수정해야함
        // 시큐리티 구현시 변경될수도
        final User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("조회된 유저가 없습니다."));

        redisTemplate.opsForList().rightPush("waiting:" + ott.getId(), user.getId());

        return true;
    }

    /*
    * 이거를 비동기로 해야할 듯?
    * */
    @Async("threadPoolTaskExecutor")
    public void partyMatch(final Long userId, final Long ottId) {
        final Ott ott = ottRepository.findById(ottId)
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        final int capacity = ott.getCapacity();

        // 커스텀 예외 수정해야함
        // 시큐리티 구현시 변경될수도
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("조회된 유저가 없습니다."));

        final List<Party> availableParty = partyRepository.findAvailablePartyByOtt(ottId, PartyStatus.AVAILABLE);

        availableParty.stream()
                .filter(party -> party.getPartyMembers().size() < ott.getCapacity())
                .findFirst()
                .ifPresent(party -> addMemberToParty(party, user, capacity));

    }

    private void addMemberToParty(final Party party, final User user, final int capacity) {

        PartyMember partyMember = PartyMember.of(user, party, LocalDateTime.now());

        party.changeStatus(capacity);
        partyRepository.save(party);
    }
}
