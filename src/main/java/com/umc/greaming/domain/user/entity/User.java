package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.auth.entity.Provider;
import com.umc.greaming.domain.user.entity.enums.UserState;
import com.umc.greaming.domain.user.entity.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "nickname", length = 30, nullable = false)
    private String nickname;

    @Column(name = "introduction", length = 350)
    private String introduction;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", nullable = false)
    private UserState userState;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Provider> providers = new ArrayList<>();

    public void updateProfile(String nickname, String introduction, String profileImageUrl) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void deactivate() {
        this.userState = UserState.INACTIVE;
    }

    public void delete() {
        this.userState = UserState.DELETED;
    }
}
