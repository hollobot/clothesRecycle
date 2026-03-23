package com.example.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件存储服务。
 */
public interface FileStorageService {

    /**
     * 上传多张图片并返回可访问 URL。
     */
    List<String> uploadImages(List<MultipartFile> files);
}
