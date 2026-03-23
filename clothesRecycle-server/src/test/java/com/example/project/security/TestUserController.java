package com.example.project.security;

import com.example.project.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限边界测试用户接口。
 */
@RestController
public class TestUserController {

    /**
     * 用户侧受保护接口。
     */
    @GetMapping("/api/user/profile")
    public Result<String> profile() {
        return Result.ok("user");
    }
}
