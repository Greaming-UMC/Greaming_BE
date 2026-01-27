package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Provider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "email", nullable = false, length = 255)
    private String email;
}