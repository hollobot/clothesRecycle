package com.example.project.controller.client;

import com.example.project.common.result.Result;
import com.example.project.model.dto.auth.LoginDto;
import com.example.project.model.dto.auth.RegisterDto;
import com.example.project.model.dto.auth.SendSmsDto;
import com.example.project.model.vo.auth.LoginVo;
import com.example.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端认证控制器。
 */
@RestController
@RequestMapping("/api/public/auth")
public class AuthClientController {

    private final AuthService authService;

    public AuthClientController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 发送注册验证码。
     */
    @PostMapping("/sms")
    public Result<Void> sendSms(@Valid @RequestBody SendSmsDto dto) {
        authService.sendRegisterSmsCode(dto);
        return Result.ok();
    }

    /**
     * 用户注册。
     */
    @PostMapping("/register")
    public Result<LoginVo> register(@Valid @RequestBody RegisterDto dto) {
        return Result.ok(authService.register(dto));
    }

    /**
     * 用户登录。
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return Result.ok(authService.login(dto));
    }
}
