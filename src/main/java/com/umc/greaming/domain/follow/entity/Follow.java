package com.umc.greaming.domain.follow.entity;
import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.follow.enums.FollowState;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follows")
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", nullable = false)
    private User following;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_state", nullable = false)
    @ColumnDefault("'COMPLETED'")
    @Builder.Default
    private FollowState state = FollowState.COMPLETED;

}
