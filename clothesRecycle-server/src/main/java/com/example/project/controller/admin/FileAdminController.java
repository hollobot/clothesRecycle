package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 管理端文件上传控制器。
 */
@RestController
@RequestMapping("/api/admin/files")
public class FileAdminController {

    /**
     * 文件存储服务。
     */
    private final FileStorageService fileStorageService;

    public FileAdminController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * 上传图片并返回可访问 URL 列表。
     *
     * @param files 图片文件列表
     * @return 图片访问地址列表
     */
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        return Result.ok(fileStorageService.uploadImages(files));
    }
}
