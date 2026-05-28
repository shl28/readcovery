package com.readcovery.dto.quote;

import com.readcovery.domain.Quote;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuoteResponse {

    private Long quoteId;
    private Long myBookId;
    private String bookTitle;
    private String content;
    private Integer page;
    private LocalDateTime createdAt;

    public static QuoteResponse from(Quote quote) {
        return QuoteResponse.builder()
                .quoteId(quote.getId())
                .myBookId(quote.getMyBook().getId())
                .bookTitle(quote.getMyBook().getBook().getTitle())
                .content(quote.getContent())
                .page(quote.getPage())
                .createdAt(quote.getCreatedAt())
                .build();
    }

}
