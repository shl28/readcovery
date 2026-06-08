package com.readcovery.service;

import com.readcovery.domain.AnalysisResult;
import com.readcovery.dto.book.BookSearchResult;
import com.readcovery.repository.AnalysisResultRepository;
import com.readcovery.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int RECOMMENDATIONS_PER_KEYWORD =  10;
    private static final int TOTAL_RECOMMENDATIONS = 5;

    private final AnalysisResultRepository analysisResultRepository;
    private final MyBookRepository myBookRepository;
    private final BookSearchService bookSearchService;

    @Transactional(readOnly = true)
    public List<BookSearchResult> recommend(Long userId) {
        List<String> keywords = analysisResultRepository
                .findTopByUser_IdOrderByCreatedAtDesc(userId)
                .map(AnalysisResult::getKeywordList)
                .orElseThrow(() -> new IllegalStateException(
                        "분석 결과가 없습니다. 먼저 분석을 진행해주세요."
                ));

        log.info("추천 시작: userId={}, keywords={}", userId, keywords);

        Set<String> ownedIsbns = myBookRepository.findByUserId(userId)
                .stream().map(myBook -> myBook.getBook().getIsbn())
                .collect(Collectors.toSet());

        List<BookSearchResult> recommendations = new ArrayList<>();
        Set<String> seenIsbns = new HashSet<>(ownedIsbns);

        for (String keyword : keywords) {
            List<BookSearchResult> searched = bookSearchService.searchBooks(
                    keyword,
                    1,
                    RECOMMENDATIONS_PER_KEYWORD
            );

            for (BookSearchResult book : searched) {
                if (book.getIsbn() == null || book.getIsbn().isBlank()) continue;
                if (seenIsbns.contains(book.getIsbn())) continue;

                recommendations.add(book);
                seenIsbns.add(book.getIsbn());

                if (recommendations.size() >= TOTAL_RECOMMENDATIONS) {
                    return recommendations;
                }
            }
        }

        return recommendations;
    }
}
