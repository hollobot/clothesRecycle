package com.example.project.controller.client;

import com.example.project.common.result.Result;
import com.example.project.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户端文件上传控制器。
 */
@RestController
@RequestMapping("/api/user/files")
public class FileClientController {

    private final FileStorageService fileStorageService;

    public FileClientController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * 上传发布物品所需的多张图片。
     */
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        return Result.ok(fileStorageService.uploadImages(files));
    }
}
