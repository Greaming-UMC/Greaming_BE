package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_interest_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserInterestTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_interest_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}