package com.readcovery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "analysis_results",
        indexes = {
                @Index(name = "idx_analysis_user", columnList = "user_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String keywords;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String personality;

    @Column(nullable = false)
    private Integer quoteCountAtAnalysis;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AnalysisResult(
            User user,
            String summary,
            String keywords,
            String personality,
            Integer quoteCountAtAnalysis,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.summary = summary;
        this.keywords = keywords;
        this.personality = personality;
        this.quoteCountAtAnalysis = quoteCountAtAnalysis;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // 인용구 개수가 크게 늘었는지 확인(5개 이상 추가됐으면 갱신)
    public boolean shouldRegenerate(int currentQuoteCount) {
        return isExpired() || (currentQuoteCount - this.quoteCountAtAnalysis >= 5);
    }
}
