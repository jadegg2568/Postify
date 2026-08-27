package ru.jadegg2568.Postify.file;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.jadegg2568.Postify.config.MinioConfig;
import ru.jadegg2568.Postify.user.User;
import ru.jadegg2568.Postify.file.event.FileUploadedEvent;
import ru.jadegg2568.Postify.file.exception.FileDeleteException;
import ru.jadegg2568.Postify.file.exception.FileDownloadException;
import ru.jadegg2568.Postify.file.exception.FileUploadException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final ApplicationEventPublisher eventPublisher;

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

    public String uploadFile(User user, MultipartFile file) {
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

            eventPublisher.publishEvent(new FileUploadedEvent(user, objectName, file.getName(), file.getSize(), file.getContentType()));
            return objectName;
        } catch (Exception e) {
            throw new FileUploadException();
        }
    }

    public String generatePresignedUrl(String objectName) {
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