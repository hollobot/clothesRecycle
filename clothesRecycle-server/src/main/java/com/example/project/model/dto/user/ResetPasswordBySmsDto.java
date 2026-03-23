package com.example.project.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端短信验证码重置密码请求。
 */
@Data
public class ResetPasswordBySmsDto {

    /**
     * 短信验证码。
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度必须为6位")
    private String smsCode;

    /**
     * 新密码。
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度需为6-64位")
    private String newPassword;
}
