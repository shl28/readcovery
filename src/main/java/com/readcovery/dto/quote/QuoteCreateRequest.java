package com.readcovery.dto.quote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuoteCreateRequest {

    @NotNull(message = "myBookId는 필수입니다.")
    private Long myBookId;

    @NotBlank(message = "인용구 내용은 필수입니다.")
    private String content;

    private Integer page;
}
