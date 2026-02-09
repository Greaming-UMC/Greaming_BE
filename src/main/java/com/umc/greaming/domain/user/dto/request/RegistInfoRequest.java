package com.umc.greaming.domain.user.dto.request;

import com.umc.greaming.domain.user.entity.enums.ArtField;
import com.umc.greaming.domain.user.entity.enums.ArtStyle;
import com.umc.greaming.domain.user.entity.enums.UsagePurpose;

import java.util.List;

public record RegistInfoRequest(
        String nickname,
        String intro,
        SpecialtyInfo specialties,
        InterestInfo interests,
        UsagePurpose usagePurpose,
        Integer weeklyGoalScore
) {

    public record SpecialtyInfo(
            List<ArtField> fields,
            ArtStyle style
    ) {}

    public record InterestInfo(
            List<ArtField> fields,
            ArtStyle style
    ) {}
}
