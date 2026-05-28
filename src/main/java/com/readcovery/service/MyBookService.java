package com.readcovery.service;

import com.readcovery.domain.Book;
import com.readcovery.domain.MyBook;
import com.readcovery.domain.User;
import com.readcovery.dto.mybook.MyBookCreateRequest;
import com.readcovery.dto.mybook.MyBookResponse;
import com.readcovery.repository.BookRepository;
import com.readcovery.repository.MyBookRepository;
import com.readcovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {

    private final MyBookRepository myBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public MyBookResponse addToMyLibrary(Long userId, MyBookCreateRequest request) {
        // 1. 이미 담은 책인지 확인
        if (myBookRepository.existsByUserIdAndBookId(userId, request.getBookId())) {
            throw new IllegalStateException("이미 서재에 담은 책입니다.");
        }

        // 2. User와 Book 조회 (없으면 예외)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));

        // 3. MyBook 생성 및 저장
        MyBook myBook = MyBook.builder()
                .user(user)
                .book(book)
                .status(request.getStatus())
                .build();

        MyBook saved = myBookRepository.save(myBook);
        return MyBookResponse.from(saved);
    }

    // 내서재 전체 조회
    public List<MyBookResponse> getMyLibrary(Long userId) {
        return myBookRepository.findByUserId(userId).stream()
                .map(MyBookResponse::from)
                .toList();
    }
}
