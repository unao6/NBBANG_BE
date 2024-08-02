package com.elice.nbbang.domain.party.service;

import static com.elice.nbbang.domain.party.entity.PartyStatus.*;

import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.exception.DuplicateParty;
import com.elice.nbbang.domain.party.exception.PartyNotFoundException;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.party.service.dto.PartyCreateServiceRequest;
import com.elice.nbbang.domain.party.service.dto.PartyUpdateServiceRequest;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PartyService {
    private final PartyRepository partyRepository;

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

    // 구현해야 함
    public void getAllParty() {
        String email = userUtil.getAuthenticatedUserEmail();
        final User user = userRepository.findByEmail(email);
        List<Party> subscribedOttByUserId = partyRepository.findSubscribedOttByUserId(user.getId());


        // 휴대폰 번호(본인인증 완료되면 추가)
        // 구독중인 Ott 이름, 파티장(nickname email), 휴대폰 번호, 파티원들 (nickname,email,휴대폰번호)
    }

    public void updatePartyOttAccount(final Long partyId, final PartyUpdateServiceRequest request) {

        // 현재 요청하는 User 와 변경하려는 Party 의 정보가 같은지 확인해야함
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(ErrorCode.NOT_FOUND_PARTY));

        party.updatePartyOttAccount(request);
        partyRepository.save(party);
    }
}
