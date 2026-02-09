package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.user WHERE c.submission = :submission",
           countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.submission = :submission")
    Page<Comment> findAllBySubmission(@Param("submission") Submission submission, Pageable pageable);
}
