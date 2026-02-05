package com.umc.greaming.domain.auth.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers", indexes = @Index(name = "idx_provider_name_email", columnList = "name, email"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Provider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "nickname", length = 100, nullable = false)
    private String nickname;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }
}
