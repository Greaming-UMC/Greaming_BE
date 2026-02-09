package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // [수정] 메서드 이름은 그대로 두고, @Query로 필드명을 명확하게 지정합니다.
    // cl.user.userId -> User 엔티티의 userId 필드 사용
    // cl.comment.id -> Comment 엔티티의 id 필드 사용
    @Query("SELECT COUNT(cl) > 0 FROM CommentLike cl WHERE cl.user.userId = :userId AND cl.comment.id = :commentId")
    boolean existsByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

}