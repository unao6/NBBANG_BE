package com.elice.nbbang.domain.party.service;

import static com.elice.nbbang.domain.party.entity.PartyStatus.*;

import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.controller.dto.MyPartyResponse;
import com.elice.nbbang.domain.party.controller.dto.PartyAdminResponse;
import com.elice.nbbang.domain.party.controller.dto.PartyDetailResponse;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.party.exception.DuplicateParty;
import com.elice.nbbang.domain.party.exception.PartyMemberNotFoundException;
import com.elice.nbbang.domain.party.exception.PartyNotFoundException;
import com.elice.nbbang.domain.party.repository.PartyMemberRepository;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.party.service.dto.PartyCreateServiceRequest;
import com.elice.nbbang.domain.party.service.dto.PartyUpdateServiceRequest;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PartyService {

    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final OttRepository ottRepository;
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final KakaoPayService kakaoPayService;

    public Long createParty(final PartyCreateServiceRequest request) {
        final Ott ott = ottRepository.findById(request.ottId())
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        // 커스텀 예외 수정해야함
        // 시큐리티 구현시 변경될수도
        final String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);

        Optional<Party> existParty = partyRepository.findByLeaderIdAndOttId(ott.getId(), user.getId());

        if (existParty.isPresent()) {
            throw new DuplicateParty(ErrorCode.DUPLICATE_PARTY);
        }

        final Party party = Party.builder()
                .ott(ott)
                .leader(user)
                .ottAccountId(request.ottAccountId())
                .ottAccountPassword(request.ottAccountPassword())
                .partyStatus(AVAILABLE)
                .build();

        partyRepository.save(party);

        return party.getId();
    }

    @Transactional(readOnly = true)
    public List<OttResponse> subscribeParty() {
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);
        List<Party> subscribedOttByUserId = partyRepository.findSubscribedOttByUserId(user.getId());

        return subscribedOttByUserId.stream()
                .map(party -> {
                    Ott ott = party.getOtt();
                    return new OttResponse(
                            ott.getId(),
                            ott.getName(),
                            ott.getPrice(),
                            ott.getCapacity()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MyPartyResponse> getMyParty() {
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);

        List<Party> subscribedOttByUserId = partyRepository.findSubscribedOttByUserId(user.getId());

        return subscribedOttByUserId.stream()
                .map(party -> {
                    Ott ott = party.getOtt();
                    return new MyPartyResponse(
                            party.getId(),
                            ott.getId(),
                            ott.getName()
                    );
                } )

                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartyAdminResponse> getAllPartyByAdmin(Pageable pageable) {

        Page<Party> subscribedOttByUserId = partyRepository.findAllPartyByAdmin(pageable);

        return subscribedOttByUserId
                .stream()
                .map(PartyAdminResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartyAdminResponse> getPartiesByEmail(String email, Pageable pageable) {
        Page<Party> partiesByEmail = partyRepository.findByEmail(email, pageable);

        return partiesByEmail
                .stream()
                .map(PartyAdminResponse::new)
                .collect(Collectors.toList());
    }

    public void updatePartyOttAccount(final Long partyId, final PartyUpdateServiceRequest request) {
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);

        // 현재 요청하는 User 와 변경하려는 Party 의 정보가 같은지 확인해야함
        Party party = partyRepository.findByPartyIdAndUserId(partyId, user.getId())
                .orElseThrow(() -> new PartyNotFoundException(ErrorCode.NOT_FOUND_PARTY));;

        party.updatePartyOttAccount(request);
        partyRepository.save(party);
    }

    @Transactional(readOnly = true)
    public PartyDetailResponse getPartyById(final Long partyId) {
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);

        final Party party = partyRepository.findPartyAndPartyMemberByPartyId(partyId);
        boolean isLeader = party.getLeader().getId().equals(user.getId());
        log.info("isLeader = {}", isLeader);

        return new PartyDetailResponse(party, isLeader);


    }

    public void withdrawParty(final Long partyId) {
        log.info("party 탈퇴 시도");
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);

        List<PartyMember> partyMemberByPartyId = partyMemberRepository.findPartyMemberByPartyId(partyId);

        PartyMember partyMember = partyMemberByPartyId.stream()
                .filter(pm -> pm.getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new PartyMemberNotFoundException(ErrorCode.NOT_FOUND_PARTY_MEMBER));

        partyMemberRepository.delete(partyMember);
        log.info("party 탈퇴 성공");
    }
}
