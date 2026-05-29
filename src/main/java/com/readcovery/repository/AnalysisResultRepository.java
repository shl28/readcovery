package com.readcovery.repository;

import com.readcovery.domain.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // 사용자의 가장 최근 분석 결과
    Optional<AnalysisResult> findTopByUser_IdOrderByCreatedAtDesc(Long userId);
}
