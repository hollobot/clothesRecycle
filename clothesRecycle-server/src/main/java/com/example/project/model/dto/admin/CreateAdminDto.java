package com.example.project.model.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增管理员请求。
 */
@Data
public class CreateAdminDto {

    @NotBlank(message = "手机号不能为空")
    @Size(max = 32, message = "手机号长度不能超过32")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过128")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @NotBlank(message = "角色不能为空")
    @Size(max = 32, message = "角色长度不能超过32")
    private String role;

    private Long campusId;
}
