package ru.jadegg2568.Postify.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.jadegg2568.Postify.config.MinioConfig;
import ru.jadegg2568.Postify.exception.file.FileDeleteException;
import ru.jadegg2568.Postify.exception.file.FileDownloadException;
import ru.jadegg2568.Postify.exception.file.FileUploadException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    @PostConstruct
    public void initBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build()
                );
                log.info("Bucket {} created", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Minio S3 with bucket " + minioConfig.getBucketName(), e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            String objectName = UUID.randomUUID().toString();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName;
        } catch (Exception e) {
            throw new FileUploadException();
        }
    }

    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            throw new FileDownloadException();
        }
    }

    public String getPresignedUrlTemporarily(String objectName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(expirySeconds)
                            .build()
            );
        } catch (Exception e) {
            throw new FileDownloadException();
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new FileDeleteException();
        }
    }
}