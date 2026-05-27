package com.readcovery.service;

import com.readcovery.domain.AuthProvider;
import com.readcovery.domain.User;
import com.readcovery.dto.user.SignupRequest;
import com.readcovery.dto.user.SignupResponse;
import com.readcovery.exception.DuplicateEmailException;
import com.readcovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .authProvider(AuthProvider.LOCAL)
                .build();

        User savedUser = userRepository.save(user);

        // 4. 응답 DTO 변환
        return SignupResponse.from(savedUser);
    }
}
