package com.umc.greaming.domain.submission.repository;

import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionTagRepository extends JpaRepository<SubmissionTag, Long> {

    @Query("select sb.tag.name from SubmissionTag sb where sb.submission.id = :submissionId")
    List<String> findTagNamesBySubmissionId(@Param("submissionId") Long submissionId);

    @Query("SELECT st FROM SubmissionTag st JOIN FETCH st.tag WHERE st.submission.id = :submissionId")
    List<SubmissionTag> findAllBySubmissionId(@Param("submissionId") Long submissionId);

    @Modifying
    @Query("DELETE FROM SubmissionTag st WHERE st.submission = :submission")
    void deleteAllBySubmission(@Param("submission") Submission submission);

}
