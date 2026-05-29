package com.readcovery.controller;

import com.readcovery.dto.quote.QuoteCreateRequest;
import com.readcovery.dto.quote.QuoteResponse;
import com.readcovery.dto.quote.QuoteUpdateRequest;
import com.readcovery.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> addQuote(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody QuoteCreateRequest request
    ) {
        QuoteResponse response = quoteService.addQuote(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getQuotes(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long myBookId
    ) {
        List<QuoteResponse> quotes = quoteService.getQuotesByMyBook(userId, myBookId);
        return ResponseEntity.ok(quotes);
    }

    @PatchMapping("/{quoteId}")
    public ResponseEntity<QuoteResponse> updateQuote(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long quoteId,
            @Valid @RequestBody QuoteUpdateRequest request
    ) {
        QuoteResponse response = quoteService.updateQuote(userId, quoteId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{quoteId}")
    public ResponseEntity<Void> deleteQuote(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long quoteId
    ) {
        quoteService.deleteQuote(userId, quoteId);
        return ResponseEntity.noContent().build();
    }
}
