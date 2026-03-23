package com.example.project.service.impl;

import com.example.project.config.MinioProperties;
import com.example.project.exception.BusinessException;
import com.example.project.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 基于 MinIO 的文件存储实现。
 */
@Service
public class MinioFileStorageServiceImpl implements FileStorageService {

    private static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";
    /**
     * 发布物品时最多上传图片数量。
     */
    private static final int MAX_IMAGE_COUNT = 6;
    /**
     * 单张图片大小上限（5MB）。
     */
    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioFileStorageServiceImpl(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("请至少上传一张图片");
        }
        if (files.size() > MAX_IMAGE_COUNT) {
            throw new BusinessException("最多上传6张图片");
        }

        ensureBucketExists();

        List<String> urls = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new BusinessException("图片文件不能为空");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX)) {
                throw new BusinessException("仅支持上传图片文件");
            }
            if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
                throw new BusinessException("单张图片大小不能超过5MB");
            }

            String objectName = buildObjectName(file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build());
            } catch (Exception e) {
                throw new BusinessException("图片上传失败");
            }

            urls.add(buildPublicUrl(objectName));
        }
        return urls;
    }

    private void ensureBucketExists() {
        String bucketName = minioProperties.getBucketName();
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            // 设置桶为公开可读，确保前端可通过 URL 直接访问图片。
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(buildPublicReadPolicy(bucketName))
                    .build());
        } catch (Exception e) {
            throw new BusinessException("存储服务不可用");
        }
    }

    /**
     * 构建 MinIO 桶公开读策略（仅允许读取对象，不开放写权限）。
     */
    private String buildPublicReadPolicy(String bucketName) {
        return "{" +
                "\"Version\":\"2012-10-17\"," +
                "\"Statement\":[{" +
                "\"Effect\":\"Allow\"," +
                "\"Principal\":{\"AWS\":[\"*\"]}," +
                "\"Action\":[\"s3:GetObject\"]," +
                "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]" +
                "}]" +
                "}";
    }

    private String buildObjectName(String originalFilename) {
        String suffix = "";
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex >= 0) {
                suffix = originalFilename.substring(lastDotIndex);
            }
        }
        return "item/" + UUID.randomUUID() + suffix;
    }

    private String buildPublicUrl(String objectName) {
        String endpoint = minioProperties.getEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        // 返回可直接访问的图片地址，便于前端在发布物品时回填到 imageUrls。
        return endpoint + "/" + minioProperties.getBucketName() + "/" + objectName;
    }
}
