package com.readcovery.controller;

import com.readcovery.dto.mybook.MyBookCreateRequest;
import com.readcovery.dto.mybook.MyBookResponse;
import com.readcovery.dto.mybook.MyBookUpdateRequest;
import com.readcovery.service.MyBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-books")
@RequiredArgsConstructor
public class MyBookController {

    private final MyBookService myBookService;

    @PostMapping
    public ResponseEntity<MyBookResponse> addToMyLibrary(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody MyBookCreateRequest request
    ) {
        MyBookResponse response = myBookService.addToMyLibrary(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MyBookResponse>> getMyLibrary(
            @AuthenticationPrincipal Long userId
    ) {
        List<MyBookResponse> library = myBookService.getMyLibrary(userId);
        return ResponseEntity.ok(library);
    }

    @GetMapping("/{myBookId}")
    public ResponseEntity<MyBookResponse> getMyBookDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long myBookId
    ) {
        MyBookResponse response = myBookService.getMyBookDetail(userId, myBookId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{myBookId}")
    public ResponseEntity<MyBookResponse> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long myBookId,
            @Valid @RequestBody MyBookUpdateRequest request
    ) {
        MyBookResponse response = myBookService.update(userId, myBookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{myBookId}")
    public ResponseEntity<Void> deleteMyBook(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long myBookId
    ) {
        myBookService.deleteMyBook(userId, myBookId);
        return ResponseEntity.noContent().build();
    }
}
