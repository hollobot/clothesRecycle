package com.example.project.service;

import com.example.project.model.dto.user.ChangePasswordDto;
import com.example.project.model.dto.user.ResetPasswordBySmsDto;
import com.example.project.model.dto.user.UpdateMyProfileDto;
import com.example.project.model.vo.user.UserProfileVo;

/**
 * 用户端资料与密码服务。
 */
public interface UserProfileService {

    /**
     * 查询当前用户资料。
     */
    UserProfileVo getProfile(Long userId);

    /**
     * 更新当前用户资料。
     */
    UserProfileVo updateProfile(Long userId, UpdateMyProfileDto dto);

    /**
     * 旧密码修改。
     */
    void changePassword(Long userId, ChangePasswordDto dto);

    /**
     * 发送重置密码短信验证码（模拟）。
     */
    void sendResetPasswordSms(Long userId);

    /**
     * 短信验证码重置密码。
     */
    void resetPasswordBySms(Long userId, ResetPasswordBySmsDto dto);
}
