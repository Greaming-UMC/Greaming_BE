package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.user.entity.enums.ArtField;
import com.umc.greaming.domain.user.entity.enums.ArtStyle;
import com.umc.greaming.domain.user.entity.enums.UsagePurpose;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_purpose", nullable = false)
    private UsagePurpose usagePurpose;

    @Column(name = "weekly_goal_score", nullable = false)
    private Integer weeklyGoalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialty_style", nullable = false)
    private ArtStyle specialtyStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_style", nullable = false)
    private ArtStyle interestStyle;

    @ElementCollection(targetClass = ArtField.class)
    @CollectionTable(name = "user_specialty_field", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "art_field")
    @Builder.Default
    private List<ArtField> specialtyFields = new ArrayList<>();

    @ElementCollection(targetClass = ArtField.class)
    @CollectionTable(name = "user_interest_field", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "art_field")
    @Builder.Default
    private List<ArtField> interestFields = new ArrayList<>();
}
