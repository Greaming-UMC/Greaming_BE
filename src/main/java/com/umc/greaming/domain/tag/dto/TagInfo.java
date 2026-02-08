package com.umc.greaming.domain.tag.dto;

import com.umc.greaming.domain.tag.entity.Tag;

public record TagInfo(
        Long tagId,
        String tagName
) {

    public static TagInfo from(Tag tag) {
        return new TagInfo(tag.getId(), tag.getName());
    }
}