package org.example.expert.domain.common.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;


@Service
public class S3Service {

    private final S3Presigner presigner;
    private final String bucketName;
    private final int expirationMinutes;

    public S3Service(
            S3Presigner presigner,
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${cloud.aws.s3.presigned-url-expiration}") int expirationMinutes
    ) {
        this.presigner = presigner;
        this.bucketName = bucketName;
        this.expirationMinutes = expirationMinutes;
    }

    public String generatePresignedUrl(String fileName) {
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(expirationMinutes))
                        .putObjectRequest(req -> req.bucket(bucketName).key(fileName))
                        .build()
        );

        return presignedRequest.url().toString();
    }

    public String generatePresignedDownloadUrl(String fileName) {
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(expirationMinutes))
                        .getObjectRequest(req -> req.bucket(bucketName).key(fileName))
                        .build()
        );

        return presignedRequest.url().toString();
    }
}
