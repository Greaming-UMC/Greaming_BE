package com.umc.greaming.domain.submission.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.comment.entity.Comment;
import com.umc.greaming.domain.comment.repository.CommentRepository;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
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
    private final TagRepository tagRepository;
    private static final int PAGE_SIZE = 30;

    public SubmissionPreviewResponse getSubmissionPreview(Long submissionId) {
        Submission submission = findSubmissionByIdOrThrow(submissionId);
        List<String> tags = submissionTagRepository.findTagNamesBySubmissionId(submissionId);
        return SubmissionPreviewResponse.from(submission, tags);
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
        List<TagInfo> tagInfos = getTagInfos(submissionId);
        return createSubmissionInfoFromEntity(submission, tagInfos);
    }

    @Transactional
    public SubmissionInfo updateSubmission(Long submissionId, SubmissionUpdateRequest request, Long userId) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));

        /*
        if (!submission.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        */

        String newThumbnailUrl = null;

        if (request.imageList() != null) {
            submissionImageRepository.deleteAllBySubmission(submission);
            // [수정] 삭제 쿼리를 즉시 실행하여 Unique Key 충돌 방지
            submissionImageRepository.flush();

            List<String> newImages = request.imageList();
            for (int i = 0; i < newImages.size(); i++) {
                SubmissionImage image = SubmissionImage.builder()
                        .submission(submission)
                        .imageUrl(newImages.get(i))
                        .sortOrder(i + 1)
                        .build();
                submissionImageRepository.save(image);
            }
            if (!newImages.isEmpty()) {
                newThumbnailUrl = newImages.get(0);
            }
        }

        submission.update(
                request.title(),
                request.caption(),
                request.visibility(),
                request.commentEnabled(),
                newThumbnailUrl
        );

        List<TagInfo> resultTags;
        if (request.tags() != null) {
            resultTags = updateTagsAndReturnInfo(submission, request.tags());
        } else {
            resultTags = getTagInfos(submissionId);
        }

        return createSubmissionInfoFromEntity(submission, resultTags);
    }

    private List<TagInfo> updateTagsAndReturnInfo(Submission submission, List<String> newTagNames) {
        submissionTagRepository.deleteAllBySubmission(submission);
        // [수정] 태그도 안전하게 즉시 삭제 반영
        submissionTagRepository.flush();

        List<TagInfo> updatedInfos = new ArrayList<>();

        for (String tagName : newTagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

            SubmissionTag submissionTag = SubmissionTag.builder()
                    .submission(submission)
                    .tag(tag)
                    .build();
            submissionTagRepository.save(submissionTag);
            updatedInfos.add(TagInfo.from(tag));
        }
        return updatedInfos;
    }

    private List<TagInfo> getTagInfos(Long submissionId) {
        return submissionTagRepository.findAllBySubmissionId(submissionId).stream()
                .map(st -> TagInfo.from(st.getTag()))
                .toList();
    }

    private SubmissionInfo createSubmissionInfoFromEntity(Submission submission, List<TagInfo> tags) {
        Long submissionId = submission.getId();
        List<String> sortedImages = submissionImageRepository.findAllBySubmissionIdOrderBySortOrderAsc(submissionId)
                .stream()
                .map(SubmissionImage::getImageUrl)
                .toList();

        boolean isLiked = false;

        return SubmissionInfo.from(submission, sortedImages, tags, isLiked);
    }

    private Submission findSubmissionByIdOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SUBMISSION_NOT_FOUND));
    }
}