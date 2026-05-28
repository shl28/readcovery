package com.readcovery.service;

import com.readcovery.dto.book.BookSearchResult;
import com.readcovery.dto.book.kakao.KakaoBookSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final WebClient kakaoWebClient;

    public List<BookSearchResult> searchBooks(String query, int page, int size) {
        URI uri = UriComponentsBuilder.fromUriString("/v3/search/book")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        log.info("카카오 책 검색 호출: query={}, page={}, size={}", query, page, size);

        KakaoBookSearchResponse response = kakaoWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(KakaoBookSearchResponse.class)
                .block();

        if (response == null || response.getDocuments() == null) {
            return Collections.emptyList();
        }

        return response.getDocuments().stream()
                .map(BookSearchResult::from)
                .toList();
    }
}