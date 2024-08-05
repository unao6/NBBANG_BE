package com.elice.nbbang.domain.party.controller;

import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.party.controller.dto.*;
import com.elice.nbbang.domain.party.service.PartyMatchService;
import com.elice.nbbang.domain.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PartyController {

    private final PartyService partyService;
    private final PartyMatchService partyMatchService;

    @PostMapping("/matching")
    public ResponseEntity<Boolean> partyMatching(@RequestBody PartyMatchRequest request) {
        return ResponseEntity.ok(partyMatchService.addPartyMatchingQueue(request.toServiceRequest()));
    }


    @PostMapping("/party")
    public ResponseEntity<Long> createParty(@RequestBody PartyCreateRequest request) {
        return ResponseEntity.ok(partyService.createParty(request.toServiceRequest()));
    }

    @GetMapping("/subscribed-otts")
    public ResponseEntity<List<OttResponse>> getSubscribedOtts() {
        return ResponseEntity.ok(partyService.subscribeParty());
    }

    @GetMapping("/party/{partyId}")
    public ResponseEntity<PartyDetailResponse> getPartyById(@PathVariable("partyId") Long partyId) {
        return ResponseEntity.ok().body(partyService.getPartyById(partyId));
    }

    @GetMapping("/my-party")
    public ResponseEntity<List<MyPartyResponse>> getMyParty() {
        return ResponseEntity.ok().body(partyService.getMyParty());
    }


    @DeleteMapping("/party-breakup/{partyId}")
    public ResponseEntity<Void> partyBreakUp(@PathVariable Long partyId) {
        partyMatchService.partyBreakup(partyId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/party/{partyId}")
    public ResponseEntity<Void> updatePartyOttAccount(@PathVariable Long partyId,
                                                      @RequestBody PartyUpdateRequest request) {
        partyService.updatePartyOttAccount(partyId, request.toServiceRequest());

        return ResponseEntity.noContent().build();
    }
}
