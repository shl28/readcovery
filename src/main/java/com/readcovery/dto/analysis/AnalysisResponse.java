package com.readcovery.dto.analysis;

import com.readcovery.domain.AnalysisResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class AnalysisResponse {

    private String summary;
    private List<String> keywords;
    private String personality;
    private Integer quoteCount;
    private LocalDateTime analyzedAt;
    private boolean fromCache;

    public static AnalysisResponse from(AnalysisResult result, boolean fromCache) {
        return AnalysisResponse.builder()
                .summary(result.getSummary())
                .keywords(splitKeywords(result.getKeywords()))
                .personality(result.getPersonality())
                .quoteCount(result.getQuoteCountAtAnalysis())
                .fromCache(fromCache)
                .build();
    }

    private static List<String> splitKeywords(String keywordsString) {
        if (keywordsString == null || keywordsString.isBlank()) {
            return List.of();
        }
        return Arrays.stream(keywordsString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
