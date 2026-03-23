package com.example.project.security;

import com.example.project.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限边界测试公共接口。
 */
@RestController
public class TestPublicController {

    /**
     * 公开接口。
     */
    @GetMapping("/api/public/ping")
    public Result<String> ping() {
        return Result.ok("pong");
    }
}
