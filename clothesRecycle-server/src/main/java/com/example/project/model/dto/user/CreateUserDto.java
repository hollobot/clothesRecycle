package com.example.project.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增用户请求。
 */
@Data
public class CreateUserDto {

    @NotBlank(message = "手机号不能为空")
    @Size(max = 32, message = "手机号长度不能超过32")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过128")
    private String password;

    @NotBlank(message = "学号不能为空")
    @Size(max = 64, message = "学号长度不能超过64")
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @NotNull(message = "校区不能为空")
    private Long campusId;

    @Size(max = 255, message = "头像地址不能超过255")
    private String avatarUrl;
}
