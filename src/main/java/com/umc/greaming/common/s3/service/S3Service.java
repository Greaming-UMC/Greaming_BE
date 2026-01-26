package com.umc.greaming.common.s3.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.s3.dto.S3PresignedUrlDto;
import com.umc.greaming.common.status.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public S3PresignedUrlDto getPresignedUrl(String prefix, String fileName) {
        try {
            String key = createPath(prefix, fileName);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return S3PresignedUrlDto.builder()
                    .url(presignedRequest.url().toString())
                    .key(key)
                    .build();
        } catch (Exception e){
            throw new GeneralException(ErrorStatus.S3_UPLOAD_FAILED);
        }
    }

    private String createPath(String prefix, String fileName) {
        String fileId = UUID.randomUUID().toString();
        return String.format("%s/%s-%s", prefix, fileId, fileName);
    }
}