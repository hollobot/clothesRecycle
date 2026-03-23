package com.example.project.service.impl;

import com.example.project.config.MinioProperties;
import com.example.project.exception.BusinessException;
import com.example.project.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
        } catch (Exception e) {
            throw new BusinessException("存储服务不可用");
        }
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
