package com.umc.greaming.domain.comment.repository;

import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.entity.Reply;
import com.umc.greaming.domain.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByCommentOrderByCreatedAtAsc(Comment comment);
}
