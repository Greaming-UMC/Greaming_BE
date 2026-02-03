package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import com.umc.greaming.domain.submission.entity.Submission;
import com.umc.greaming.domain.submission.entity.SubmissionImage;
import com.umc.greaming.domain.submission.repository.SubmissionImageRepository;
import com.umc.greaming.domain.submission.repository.SubmissionRepository;
import com.umc.greaming.domain.submission.repository.SubmissionTagRepository;
import com.umc.greaming.domain.tag.dto.TagInfo;
import com.umc.greaming.domain.tag.entity.SubmissionTag;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionQueryService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final CommentRepository commentRepository;
    private final SubmissionImageRepository submissionImageRepository;
    private final S3Service s3Service;
    private final TagRepository tagRepository;
    private static final int PAGE_SIZE = 30;

    private Submission findSubmissionByIdOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    }

    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        String thumbnailUrl = s3Service.getPublicUrl(submission.getThumbnailKey());

        return SubmissionPreviewResponse.from(submission, thumbnailUrl, tags);
    }

    public SubmissionDetailResponse getSubmissionDetail(Long submissionId, int page) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        List<TagInfo> tagInfos = getTagInfos(submissionId);
        SubmissionInfo submissionInfo = createSubmissionInfoFromEntity(submission, tagInfos);

        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findAllBySubmission(submission, pageable);

        return SubmissionDetailResponse.from(submissionInfo, commentPage);
    }

    public SubmissionInfo getSubmissionInfo(Long submissionId) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        return createSubmissionInfoFromEntity(submission);
    }

    private SubmissionInfo createSubmissionInfoFromEntity(Submission submission) {
        Long submissionId = submission.getId();

        List<String> sortedImageUrls = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(image -> s3Service.getPublicUrl(image.getImageKey()))
                .toList();

        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);

        String profileImageUrl = s3Service.getPublicUrl(submission.getUser().getProfileImageKey());

        boolean isLiked = false;

        return SubmissionInfo.from(submission, profileImageUrl, sortedImageUrls, tags, isLiked);
    }

}
