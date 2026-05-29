package com.readcovery.controller;

import com.readcovery.dto.analysis.AnalysisResponse;
import com.readcovery.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyze(
            @AuthenticationPrincipal Long userId
    ) {
        AnalysisResponse response = analysisService.analyze(userId);
        return ResponseEntity.ok(response);
    }
}
