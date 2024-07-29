package com.elice.nbbang.domain.party.service;

import static com.elice.nbbang.domain.party.entity.PartyStatus.*;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.exception.PartyNotFoundException;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.party.service.dto.PartyCreateServiceRequest;
import com.elice.nbbang.domain.party.service.dto.PartyUpdateServiceRequest;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PartyService {
    private final PartyRepository partyRepository;

    private final OttRepository ottRepository;
    private final UserRepository userRepository;

    public Long createParty(final PartyCreateServiceRequest request) {
        final Ott ott = ottRepository.findById(request.ottId())
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        // 커스텀 예외 수정해야함
        // 시큐리티 구현시 변경될수도
        final User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("조회된 유저가 없습니다."));

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

    public void updatePartyOttAccount(final Long partyId, final PartyUpdateServiceRequest request) {

        // 현재 요청하는 User 와 변경하려는 Party 의 정보가 같은지 확인해야함
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(ErrorCode.NOT_FOUND_PARTY));

        party.updatePartyOttAccount(request);
        partyRepository.save(party);
    }

    public void partyBreakup(final Long partId) {

    }

}
