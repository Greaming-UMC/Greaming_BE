package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllBySubmission(Submission submission, Pageable pageable);
}
