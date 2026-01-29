package com.umc.greaming.domain.circle.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.circle.enums.CircleMemberRole;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "circle_members")
public class CircleMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "circle_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id", nullable = false)
    private Circle circle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @ColumnDefault("'MEMBER'")
    @Builder.Default
    private CircleMemberRole role = CircleMemberRole.MEMBER;

}