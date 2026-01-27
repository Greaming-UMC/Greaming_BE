package com.umc.greaming.domain.submission.dto.request;

import java.util.List;


public record SubmissionUpdateRequest(
        String title,
        String caption,
        List<String> tags,
        List<String> imageList
) {}
