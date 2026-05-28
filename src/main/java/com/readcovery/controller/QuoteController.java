package com.readcovery.controller;

import com.readcovery.dto.quote.QuoteCreateRequest;
import com.readcovery.dto.quote.QuoteResponse;
import com.readcovery.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> addQuote(
            @Valid @RequestBody QuoteCreateRequest request
    ) {
        QuoteResponse response = quoteService.addQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getQuotes(
            @RequestParam Long myBookId
    ) {
        List<QuoteResponse> quotes = quoteService.getQuotesByMyBook(myBookId);
        return ResponseEntity.ok(quotes);
    }
}
