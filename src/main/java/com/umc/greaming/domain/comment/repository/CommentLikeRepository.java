package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT COUNT(cl) > 0 FROM CommentLike cl WHERE cl.user.userId = :userId AND cl.comment.id = :commentId")
    boolean existsByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user.userId = :userId AND cl.comment.id IN :commentIds")
    List<Long> findLikedCommentIdsByUserIdAndCommentIds(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);
}
