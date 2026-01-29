package com.umc.greaming.domain.report.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.report.enums.ReportReason;
import com.umc.greaming.domain.report.enums.ReportStatus;
import com.umc.greaming.domain.report.enums.ReportTargetType;
import com.umc.greaming.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 20)
    private ReportReason reason;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @ColumnDefault("'OPEN'")
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}