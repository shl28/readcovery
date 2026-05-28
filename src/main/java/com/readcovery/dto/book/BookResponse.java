package com.readcovery.dto.book;

import com.readcovery.domain.Book;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookResponse {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String thumbnail;
    private String contents;
    private LocalDateTime createdAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .thumbnail(book.getThumbnail())
                .contents(book.getContents())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
