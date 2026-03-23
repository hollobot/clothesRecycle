package com.example.project.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户端更新个人资料请求。
 */
@Data
public class UpdateMyProfileDto {

    /**
     * 姓名。
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    /**
     * 学号。
     */
    @NotBlank(message = "学号不能为空")
    @Size(max = 32, message = "学号长度不能超过32")
    private String studentId;

    /**
     * 所属校区 ID。
     */
    @NotNull(message = "校区不能为空")
    private Long campusId;

    /**
     * 头像 URL。
     */
    @Size(max = 255, message = "头像地址不能超过255")
    private String avatarUrl;
}
