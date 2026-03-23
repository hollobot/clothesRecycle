package com.example.project.controller.client;

import com.example.project.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端公开接口控制器。
 * <p>承载无需登录的 /api/public/** 路由。</p>
 */
@RestController
@RequestMapping("/api/public")
public class PublicClientController {

    /**
     * 公共连通性检查接口。
     */
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("pong");
    }
}
