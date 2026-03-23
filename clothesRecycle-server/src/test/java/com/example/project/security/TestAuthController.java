package com.example.project.security;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限边界测试登录接口。
 */
@RestController
@RequestMapping("/api/public/test-auth")
public class TestAuthController {

    /**
     * 按角色签发测试 token。
     */
    @GetMapping("/token/{role}")
    public Result<String> token(@PathVariable String role) {
        String token = StpUtil.createLoginSession(role + "_1");
        return Result.ok(token);
    }
}
