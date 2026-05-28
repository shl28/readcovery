package com.readcovery.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookRegisterRequest {

    @NotBlank(message = "ISBN은 필수입니다.")
    private String isbn;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String author;
    private String publisher;
    private String thumbnail;
    private String contents;
}
