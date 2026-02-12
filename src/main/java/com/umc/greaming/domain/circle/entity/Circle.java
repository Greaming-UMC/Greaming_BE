package com.umc.greaming.domain.circle.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "circles")
public class Circle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "circle_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "profile_image_key", length = 255)
    private String profileImageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User leader;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isDeleted = false;

}
