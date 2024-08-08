package com.elice.nbbang.domain.party.service;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.MatchingType;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.party.entity.PartyStatus;
import com.elice.nbbang.domain.party.exception.PartyNotFoundException;
import com.elice.nbbang.domain.party.repository.PartyMemberRepository;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.party.service.dto.PartyMatchServiceRequest;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.payment.service.AccountService;
import com.elice.nbbang.domain.payment.service.BootPayService;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.config.EncryptUtils;
import com.elice.nbbang.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.elice.nbbang.domain.payment.entity.enums.PaymentType.*;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PartyMatchService {

    private final OttRepository ottRepository;
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final CardRepository cardRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KakaoPayService kakaoPayService;
    private final AccountService accountService;
    private final BootPayService bootPayService;
    private final EncryptUtils encryptUtils;
    private final UserUtil userUtil;

    /*
    * 많은 수의 사용자가 동시에 자동 매칭을 시켯을 때 동시성 문제가 없나?
    * 있다면 처리를 어떻게 해야할까?
    * */
    public boolean addPartyMatchingQueue(final PartyMatchServiceRequest request) {
        log.info("addPartyMatchingQueue : {}", "Redis 큐에 넣기");
        final Ott ott = ottRepository.findById(request.ottId())
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        final User user = getAuthenticatedUser();

        String requestString = createRequestValue(user.getId(), MatchingType.MATCHING, ott.getId());
        String duplicatedString = createDuplicateValue(user.getId(), ott.getId());

        String listKey = "waiting:" + ott.getId();
        String setKey = "waiting_set:" + ott.getId();

        if (isDuplicateMatching(setKey, duplicatedString)) {
            log.info("중복된 레디스");
            return false;
        }

        addPartyMatchingQueue(setKey, listKey, requestString, duplicatedString);
        log.info("레디스 큐 넣기 성공");
        return true;
    }

    /*
    * 이거를 비동기로 해야할 듯?
    * 동시성 문제 해결?
    * */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> partyMatch(final Long userId, final MatchingType type, final Long ottId) throws Exception {
        final Ott ott = ottRepository.findById(ottId)
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        final int capacity = ott.getCapacity();

        Card card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("조회된 카드가 없습니다."));

        // 커스텀 예외 수정해야함
        // 시큐리티 구현시 변경될수도
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("조회된 유저가 없습니다."));

        final List<Party> partyByOtt = partyRepository.findAvailablePartyByOtt(ottId, PartyStatus.AVAILABLE);

        Optional<Party> availableParty = partyByOtt.stream()
                .filter(party -> party.getPartyMembers().size() < ott.getCapacity())
                .findFirst();
        /*
        * type 이 REMATCHING 이면 결제 하지말고 가능한 파티하고만 매칭
        * type 이 MATCHING 이면 결제 하고 파티 매칭
        * */
        if (availableParty.isPresent()) {
            Party party = availableParty.get();
            if (type.equals(MatchingType.MATCHING)) {
                // 카드 결제 서비스 로직여기서 시도하고 결제가 완료되면 Party, PartyMember 관계 맺기
                if (card.getPaymentType().equals(CARD)) { // 카드 결제
                    PaymentReserve reserve = PaymentReserve.builder()
                        .billingKey(card.getBillingKey())
                        .ott(ott)
                        .paymentSubscribedAt(LocalDateTime.now())
                        .build();
                    try {
                        bootPayService.reservePayment(reserve);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // 카카오페이 결제
                    log.info("결제 시도");
                    kakaoPayService.subscription(userId, ottId);
                    log.info("결제 성공");

                }
                addPartyMemberToParty(party, ott, user, capacity);
            } else {
                // 원래 있는 PartyMember 에서 새로운 Party 를 부여하는 메서드
                PartyMember partyMember = partyMemberRepository.findPartyMemberByOttIdAndUserId(
                        ott.getId(), user.getId());
                /*
                * int 형으로 날짜 일수 차이를 계산
                * 일수 차이 -> 결제 로직에 넣어주기
                * */
                partyMember.setParty(party);
            }
        } else {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);

    }

    @Transactional
    public void partyBreakup(final Long partyId) {
        User user = getAuthenticatedUser();

        Party party = partyRepository.findByPartyIdAndUserId(partyId, user.getId())
                .orElseThrow(() -> new PartyNotFoundException(ErrorCode.NOT_FOUND_PARTY));

        // 파티장 부분정산 실행
        accountService.caculatePartialSettlement(party);

        // 파티 멤버 삭제 및 대기 큐에 추가
        List<PartyMember> partyMembers = partyMemberRepository.findByPartyIdWithPartyAndUser(partyId);

        for (PartyMember member : partyMembers) {
            addPartyPriorityQueue(party.getOtt().getId(), MatchingType.REMATCHING, member.getUser().getId());
            member.withdrawParty();
        }
        partyRepository.delete(party);

        // 추가적인 로직 (예: 사용자에게 알림 보내기 등)

    }

    private void addPartyMemberToParty(final Party party, final Ott ott, final User user, final int capacity) {

        PartyMember partyMember = PartyMember.of(user, party, ott, LocalDateTime.now());

        party.changeStatus(capacity);
        partyMemberRepository.save(partyMember);
    }

    private void addPartyPriorityQueue(Long ottId, MatchingType type, Long userId) {
        redisTemplate.opsForSet().add("waiting_set:" + ottId, createDuplicateValue(userId, ottId));
        redisTemplate.opsForList().leftPush("waiting:" + ottId, createRequestValue(userId, type, ottId));
    }

    private void addPartyMatchingQueue(String setKey, String listKey, String requestString, String duplicatedString) {
        redisTemplate.opsForSet().add(setKey, duplicatedString);
        redisTemplate.opsForList().rightPush(listKey, requestString);
    }

    private boolean isDuplicateMatching(String setKey, String requestString) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(setKey, requestString));
    }

    private String createRequestValue(Long userId, MatchingType type, Long ottId) {
        return userId + "," + type + "," + ottId;
    }

    private String createDuplicateValue(Long userId, Long ottId) {
        return userId + "," + ottId;
    }

    private User getAuthenticatedUser() {
        final String email = userUtil.getAuthenticatedUserEmail();
        return userRepository.findByEmail(email);
    }

}
