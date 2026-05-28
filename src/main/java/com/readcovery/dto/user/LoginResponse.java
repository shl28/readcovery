package com.readcovery.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String nickname;

    public static LoginResponse of(String accessToken, Long userId, String nickname) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .nickname(nickname)
                .build();
    }
}
