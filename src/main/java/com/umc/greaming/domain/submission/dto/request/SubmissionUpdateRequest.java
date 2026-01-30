package com.umc.greaming.domain.submission.dto.request;

import com.umc.greaming.domain.submission.enums.SubmissionVisibility;

import java.util.List;


public record SubmissionUpdateRequest(
        String title,
        String caption,
        SubmissionVisibility visibility,
        Boolean commentEnabled,
        List<String> tags,
        List<String> imageList
) {}
