package com.readcovery.controller;

import com.readcovery.dto.book.BookRegisterRequest;
import com.readcovery.dto.book.BookResponse;
import com.readcovery.dto.book.BookSearchResult;
import com.readcovery.service.BookSearchService;
import com.readcovery.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookSearchService bookSearchService;
    private final BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<List<BookSearchResult>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<BookSearchResult> results = bookSearchService.searchBooks(query, page, size);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<BookResponse> registerBook(
            @Valid @RequestBody BookRegisterRequest request
    ) {
        BookResponse response = bookService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
