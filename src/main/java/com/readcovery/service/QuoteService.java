package com.readcovery.service;

import com.readcovery.domain.MyBook;
import com.readcovery.domain.Quote;
import com.readcovery.dto.quote.QuoteCreateRequest;
import com.readcovery.dto.quote.QuoteResponse;
import com.readcovery.repository.MyBookRepository;
import com.readcovery.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final MyBookRepository myBookRepository;

    @Transactional
    public QuoteResponse addQuote(QuoteCreateRequest request) {
        // 1. MyBook 존재확인
        MyBook myBook = myBookRepository.findById(request.getMyBookId())
                .orElseThrow(() -> new IllegalArgumentException("서재에서 책을 찾을 수 없습니다."));

        // 2. Quote 생성 및 저장
        Quote quote = Quote.builder()
                .myBook(myBook)
                .content(request.getContent())
                .page(request.getPage())
                .build();

        Quote saved = quoteRepository.save(quote);
        return QuoteResponse.from(saved);
    }

    // 특정 서재 책의 인용구 목록
    public List<QuoteResponse> getQuotesByMyBook(Long myBookId) {
        return quoteRepository.findByMyBookId(myBookId).stream()
                .map(QuoteResponse::from)
                .toList();
    }
}
