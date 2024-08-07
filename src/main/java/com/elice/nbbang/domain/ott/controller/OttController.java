package com.elice.nbbang.domain.ott.controller;

import com.elice.nbbang.domain.ott.controller.dto.OttCreateRequest;
import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.ott.controller.dto.OttUpdateRequest;
import com.elice.nbbang.domain.ott.service.OttService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OttController {
    private final OttService ottService;

    @PostMapping("/admin/ott")
    public ResponseEntity<Long> createOtt(@RequestBody final OttCreateRequest ottCreateRequest) {

        Long ottId = ottService.createOtt(ottCreateRequest.toServiceRequest());

        return ResponseEntity.ok(ottId);
    }

    @GetMapping("/ott/all")
    public ResponseEntity<List<OttResponse>> getAllOtt() {

        return ResponseEntity.ok(ottService.getAllOtt());
    }

    @GetMapping("/ott/{ottId}")
    public ResponseEntity<OttResponse> getOttById(@PathVariable final Long ottId) {

        return ResponseEntity.ok(ottService.getOttById(ottId));
    }

    @PutMapping("/admin/ott")
    public ResponseEntity<Void> updateOtt(@RequestBody final OttUpdateRequest ottUpdateRequest) {
        ottService.updateOtt(ottUpdateRequest.toServiceRequest());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/ott/{ottId}")
    public ResponseEntity<Void> deleteOtt(@PathVariable final Long ottId) {
        ottService.deleteOtt(ottId);
        return ResponseEntity.noContent().build();

    }
}

