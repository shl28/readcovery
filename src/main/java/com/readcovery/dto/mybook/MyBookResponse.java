package com.readcovery.dto.mybook;

import com.readcovery.domain.MyBook;
import com.readcovery.domain.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyBookResponse {

    private Long myBookId;
    private Long bookId;
    private String bookTitle;
    private String bookThumbnail;
    private ReadingStatus status;
    private Integer rating;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;

    public static MyBookResponse from(MyBook myBook) {
        return MyBookResponse.builder()
                .myBookId(myBook.getId())
                .bookId(myBook.getBook().getId())
                .bookTitle(myBook.getBook().getTitle())
                .bookThumbnail(myBook.getBook().getThumbnail())
                .status(myBook.getStatus())
                .rating(myBook.getRating())
                .startedAt(myBook.getStartedAt())
                .finishedAt(myBook.getFinishedAt())
                .createdAt(myBook.getCreatedAt())
                .build();
    }
}
