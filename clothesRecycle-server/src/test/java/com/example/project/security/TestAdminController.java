package com.example.project.security;

import com.example.project.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限边界测试管理接口。
 */
@RestController
public class TestAdminController {

    /**
     * 管理侧受保护接口。
     */
    @GetMapping("/api/admin/dashboard")
    public Result<String> dashboard() {
        return Result.ok("admin");
    }
}
