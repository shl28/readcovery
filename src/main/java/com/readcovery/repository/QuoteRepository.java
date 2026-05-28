package com.readcovery.repository;

import com.readcovery.domain.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // 특정 MyBook의 인용구 전체 조회
    List<Quote> findByMyBookId(Long myBookId);

    // 특정 사용자의 모든 인용구 조회
    List<Quote> findByMyBook_UserId(Long userId);
}
