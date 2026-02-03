package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.user.enums.UserStatus;
import com.umc.greaming.domain.user.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name="nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "introduction", length = 350)
    private String introduction;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "Visibility", nullable = false)
    private Visibility visibility;
}