package com.example.project.model.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新管理员请求。
 */
@Data
public class UpdateAdminDto {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @NotBlank(message = "角色不能为空")
    @Size(max = 32, message = "角色长度不能超过32")
    private String role;

    private Long campusId;
}
