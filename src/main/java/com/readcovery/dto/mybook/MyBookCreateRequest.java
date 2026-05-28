package com.readcovery.dto.mybook;

import com.readcovery.domain.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyBookCreateRequest {

    // 임시: JWT 도입 후 변경 예정
    @NotNull(message = "userId는  필수입니다.")
    private Long userId;

    @NotNull(message = "bookId는 필수입니다.")
    private Long bookId;

    @NotNull(message = "독서 상태는 필수입니다.")
    private ReadingStatus status;
}
