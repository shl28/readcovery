package com.readcovery.dto.quote;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuoteUpdateRequest {

    @NotBlank(message = "인용구 내용은 필수입니다.")
    private String content;

    private Integer page;
}
