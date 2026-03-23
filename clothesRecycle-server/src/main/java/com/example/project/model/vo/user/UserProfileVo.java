package com.example.project.model.vo.user;

import lombok.Data;

/**
 * 用户个人资料视图对象。
 */
@Data
public class UserProfileVo {

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 手机号（账号）。
     */
    private String phone;

    /**
     * 姓名。
     */
    private String name;

    /**
     * 学号。
     */
    private String studentId;

    /**
     * 校区 ID。
     */
    private Long campusId;

    /**
     * 头像 URL。
     */
    private String avatarUrl;
}
