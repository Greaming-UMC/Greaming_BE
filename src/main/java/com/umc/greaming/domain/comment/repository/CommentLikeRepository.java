package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.CommentLike;
import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByUserAndComment(User user, Comment comment);

    void deleteByUserAndComment(User user, Comment comment);

    @Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user.userId = :userId AND cl.comment.id IN :commentIds")
    List<Long> findLikedCommentIdsByUserIdAndCommentIds(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);
}
