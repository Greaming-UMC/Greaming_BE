package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.tag.entity.SubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionTagRepository extends JpaRepository<SubmissionTag, Long> {

    @Query("select sb.tag.name from SubmissionTag sb where sb.submission.id = :submissionId")
    List<String> findTagNamesBySubmissionId(@Param("submissionId") Long submissionId);
}
