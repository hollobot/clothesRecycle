package com.example.project.service;

import com.example.project.model.dto.auth.LoginDto;
import com.example.project.model.dto.auth.RegisterDto;
import com.example.project.model.dto.auth.SendSmsDto;
import com.example.project.model.vo.auth.LoginVo;

/**
 * 认证业务接口。
 */
public interface AuthService {

    void sendRegisterSmsCode(SendSmsDto dto);

    LoginVo register(RegisterDto dto);

    LoginVo login(LoginDto dto);
}
