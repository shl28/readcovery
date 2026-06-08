package com.readcovery.controller;

import com.readcovery.dto.book.BookSearchResult;
import com.readcovery.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<BookSearchResult>> recommend(
            @AuthenticationPrincipal Long userId
    ) {
        List<BookSearchResult> recommendations = recommendationService.recommend(userId);
        return ResponseEntity.ok(recommendations);
    }
}
