package com.readcovery.controller;

import com.readcovery.dto.mybook.MyBookCreateRequest;
import com.readcovery.dto.mybook.MyBookResponse;
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
}
