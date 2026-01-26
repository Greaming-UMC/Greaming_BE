package com.umc.greaming.common.s3.controller;

import com.umc.greaming.common.s3.dto.S3PresignedUrlDto;
import com.umc.greaming.common.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<S3PresignedUrlDto> getPresignedUrl(
            @RequestParam String prefix,
            @RequestParam String fileName
    ) {
        return ResponseEntity.ok(s3Service.getPresignedUrl(prefix, fileName));
    }
}