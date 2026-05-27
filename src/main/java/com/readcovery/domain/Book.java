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
@Table(name = "books", indexes = {@Index(name = "idx_books_isbn", columnList = "isbn"), @Index(name = "idx_books_title", columnList = "title")})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(length = 500)
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Book(String isbn, String title, String author, String publisher, String thumbnail, String contents) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.contents = contents;
    }
}
