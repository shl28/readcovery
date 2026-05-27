package com.readcovery.dto.user;

import com.readcovery.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SignupResponse {

    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
