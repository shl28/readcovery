package com.readcovery.service;

import com.readcovery.domain.Book;
import com.readcovery.dto.book.BookRegisterRequest;
import com.readcovery.dto.book.BookResponse;
import com.readcovery.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse register(BookRegisterRequest request) {
        // 1. ISBN으로 이미 등록된 책인지 확인
        Book book = bookRepository.findByIsbn(request.getIsbn())
                .orElseGet(() -> saveNewBook(request));

        return BookResponse.from(book);
    }

    private Book saveNewBook(BookRegisterRequest request) {
        log.info("새 책 등록: isbn={}, title={}", request.getIsbn(), request.getTitle());

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .thumbnail(request.getThumbnail())
                .contents(request.getContents())
                .build();

        return bookRepository.save(book);
    }
}
