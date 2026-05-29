package com.readcovery.service;

import com.readcovery.domain.MyBook;
import com.readcovery.domain.Quote;
import com.readcovery.dto.quote.QuoteCreateRequest;
import com.readcovery.dto.quote.QuoteResponse;
import com.readcovery.dto.quote.QuoteUpdateRequest;
import com.readcovery.repository.MyBookRepository;
import com.readcovery.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    public QuoteResponse addQuote(Long userId, QuoteCreateRequest request) {
        // 1. MyBook 존재확인
        MyBook myBook = myBookRepository.findById(request.getMyBookId())
                .orElseThrow(() -> new IllegalArgumentException("서재에서 책을 찾을 수 없습니다."));

        // 소유권 검증
        validateOwnership(myBook, userId);

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
    public List<QuoteResponse> getQuotesByMyBook(Long userId, Long myBookId) {
        MyBook myBook = myBookRepository.findById(myBookId)
                .orElseThrow(() -> new IllegalArgumentException("서재에서 책을 찾을 수 없습니다."));

        validateOwnership(myBook, userId);

        return quoteRepository.findByMyBookId(myBookId).stream()
                .map(QuoteResponse::from)
                .toList();
    }

    // 소유권 검증 메서드
    private void validateOwnership(MyBook myBook, Long userId) {
        if (!myBook.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 서재에만 접근할 수 있습니다.");
        }
    }

    @Transactional
    public QuoteResponse updateQuote(Long userId, Long quoteId, QuoteUpdateRequest request) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다."));

        // 소유권 검증
        validateOwnership(quote.getMyBook(), userId);

        // 수정
        quote.edit(request.getContent(), request.getPage());

        return QuoteResponse.from(quote);
    }

    @Transactional
    public void deleteQuote(Long userId, Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다."));

        validateOwnership(quote.getMyBook(), userId);

        quoteRepository.delete(quote);
    }
}
