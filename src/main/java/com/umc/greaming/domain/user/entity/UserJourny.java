package com.umc.greaming.domain.user.entity;

import com.umc.greaming.common.base.BaseEntity;
import com.umc.greaming.domain.challenge.enums.JourneyLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_journeys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserJourny extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_journey_id")
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "journey_level", nullable = false, length = 20)
    @Builder.Default
    private JourneyLevel journeyLevel = JourneyLevel.SKETCHER;
    
    @Column(name = "gold_medal", nullable = false)
    @Builder.Default
    private Integer goldMedal = 0;
    
    @Column(name = "silver_medal", nullable = false)
    @Builder.Default
    private Integer silverMedal = 0;
    
    @Column(name = "bronze_medal", nullable = false)
    @Builder.Default
    private Integer bronzeMedal = 0;
    
    @Column(name = "goal_score", nullable = false)
    @Builder.Default
    private Integer goalScore = 0;
    
    /**
     * 메달 추가
     */
    public void addMedal(String medalType) {
        switch (medalType) {
            case "GOLD" -> this.goldMedal++;
            case "SILVER" -> this.silverMedal++;
            case "BRONZE" -> this.bronzeMedal++;
        }
    }
    
    /**
     * 총 메달 개수
     */
    public int getTotalMedals() {
        return goldMedal + silverMedal + bronzeMedal;
    }
}
