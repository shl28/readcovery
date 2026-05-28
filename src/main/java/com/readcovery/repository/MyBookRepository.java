package com.readcovery.repository;

import com.readcovery.domain.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {

    // 특정 사용자의 서재 전체 조회
    List<MyBook> findByUserId(Long userId);

    // 특정 사용자가 특정 책을 이미 담았는지 확인
    Optional<MyBook> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
