package com.readcovery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "my_books",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_my_books_user_book",
                        columnNames = {"user_id", "book_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReadingStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private Integer rating;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private MyBook(User user, Book book, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.status = status;
    }

    // 독서 상태 변경
    public void changeStatus(ReadingStatus newStatus) {
        this.status = newStatus;
        if (newStatus == ReadingStatus.READING && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        if (newStatus == ReadingStatus.DONE) {
            this.finishedAt = LocalDateTime.now();
        }
    }

    // 별점 부여
    public void rate(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        }
        this.rating = rating;
    }
}
