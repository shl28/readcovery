package com.readcovery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_provider", columnList = "auth_provider, provider_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET status = 'DELETED', deleted_at = NOW() WHERE id = ?")
@SQLRestriction("status != 'DELETED'")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider authProvider;

    @Column(length = 100)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder
    private User(String email, String password, String nickname,
                 AuthProvider authProvider, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
