package com.readcovery.dto.book;

import com.readcovery.dto.book.kakao.KakaoBookSearchResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookSearchResult {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String thumbnail;
    private String contents;

    public static BookSearchResult from(KakaoBookSearchResponse.Document document) {

        // Isbn 13자리(신버전)추출
        String isbn = extractIsbn13(document.getIsbn());

        // 저자 여러명은 "," 문자열로 합치기
        String author = document.getAuthors() == null || document.getAuthors().isEmpty()
                ? "저자 미상"
                : String.join(", ", document.getAuthors());

        return BookSearchResult.builder()
                .isbn(isbn)
                .title(document.getTitle())
                .author(author)
                .publisher(document.getPublisher())
                .thumbnail(document.getThumbnail())
                .contents(document.getContents())
                .build();
    }

    private static String extractIsbn13(String isbnString) {
        if (isbnString == null || isbnString.isBlank()) {
            return null;
        }

        String[] parts = isbnString.split(" ");
        for (String part : parts) {
            if (part.length() == 13) {
                return part;
            }
        }

        return parts[0];
    }
}
