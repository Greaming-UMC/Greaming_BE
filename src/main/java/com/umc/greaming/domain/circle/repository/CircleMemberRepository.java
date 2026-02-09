package com.umc.greaming.domain.circle.repository;

import com.umc.greaming.domain.circle.entity.Circle;
import com.umc.greaming.domain.circle.entity.CircleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CircleMemberRepository extends JpaRepository<CircleMember, Long> {

    @Query("SELECT cm.circle FROM CircleMember cm " +
            "WHERE cm.user.userId = :userId " +
            "AND cm.circle.isDeleted = false") // 삭제된 서클 제외
    Optional<Circle> findCircleByUserId(@Param("userId") Long userId);
}