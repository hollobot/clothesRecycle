package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.exception.BusinessException;
import com.example.project.model.dto.user.ChangePasswordDto;
import com.example.project.model.dto.user.ResetPasswordBySmsDto;
import com.example.project.model.dto.user.UpdateMyProfileDto;
import com.example.project.model.vo.user.UserProfileVo;
import com.example.project.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端账户资料控制器。
 */
@RestController
@RequestMapping("/api/user/profile")
public class UserProfileClientController {

    private final UserProfileService userProfileService;

    public UserProfileClientController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * 查询当前登录用户资料。
     */
    @GetMapping
    public Result<UserProfileVo> getProfile() {
        return Result.ok(userProfileService.getProfile(getCurrentUserId()));
    }

    /**
     * 更新当前登录用户资料。
     */
    @PutMapping
    public Result<UserProfileVo> updateProfile(@Valid @RequestBody UpdateMyProfileDto dto) {
        return Result.ok(userProfileService.updateProfile(getCurrentUserId(), dto));
    }

    /**
     * 使用旧密码修改密码。
     */
    @PostMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        userProfileService.changePassword(getCurrentUserId(), dto);
        return Result.ok();
    }

    /**
     * 发送短信验证码（模拟发送，验证码打印在后端控制台）。
     */
    @PostMapping("/password/sms/send")
    public Result<Void> sendResetPasswordSms() {
        userProfileService.sendResetPasswordSms(getCurrentUserId());
        return Result.ok();
    }

    /**
     * 使用短信验证码重置密码。
     */
    @PostMapping("/password/sms/reset")
    public Result<Void> resetPasswordBySms(@Valid @RequestBody ResetPasswordBySmsDto dto) {
        userProfileService.resetPasswordBySms(getCurrentUserId(), dto);
        return Result.ok();
    }

    /**
     * 解析当前登录用户 ID，兼容 Sa-Token 登录态对象与字符串类型。
     */
    private Long getCurrentUserId() {
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException("用户登录态无效，请重新登录");
        }
    }
}
