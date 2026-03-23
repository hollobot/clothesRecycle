package com.example.project.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求。
 */
@Data
public class UpdateUserDto {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @NotNull(message = "校区不能为空")
    private Long campusId;

    @Size(max = 255, message = "头像地址不能超过255")
    private String avatarUrl;
}
