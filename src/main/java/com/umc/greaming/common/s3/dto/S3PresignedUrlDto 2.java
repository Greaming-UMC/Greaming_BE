package com.umc.greaming.common.s3.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3PresignedUrlDto {
    private String url;
    private String key;
}
