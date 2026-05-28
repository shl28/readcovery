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
        name = "quotes",
        indexes = {
                @Index(name = "idx_quotes_my_book", columnList = "my_book_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_book_id", nullable = false)
    private MyBook myBook;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer page;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Quote(MyBook myBook, String content, Integer page) {
        this.myBook = myBook;
        this.content = content;
        this.page = page;
    }

    // 인용구 내용 수정
    public void edit(String content, Integer page) {
        this.content = content;
        this.page = page;
    }
}
