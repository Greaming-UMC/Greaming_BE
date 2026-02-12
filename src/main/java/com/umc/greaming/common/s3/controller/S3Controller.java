package com.umc.greaming.common.s3.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.s3.dto.S3PresignedUrlDto;
import com.umc.greaming.common.s3.service.S3Service;
import com.umc.greaming.common.status.success.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Common - S3 Image Upload", description = "공통 이미지 업로드 API")
@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "Presigned URL 발급", description = "이미지 업로드를 위한 Presigned URL을 발급합니다.")
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<S3PresignedUrlDto>> getPresignedUrl(
            @Parameter(description = "저장할 폴더 이름 (예: profile, post, submission)", example = "profile")
            @RequestParam String prefix,
            @Parameter(description = "업로드할 파일의 원래 이름", example = "cat.jpg")
            @RequestParam String fileName
    ) {
        S3PresignedUrlDto result = s3Service.getPresignedUrl(prefix, fileName);

        return ApiResponse.success(SuccessStatus.S3_UPLOAD_SUCCESS, result);
    }
}
