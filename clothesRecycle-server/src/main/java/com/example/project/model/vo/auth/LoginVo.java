package com.example.project.model.vo.auth;

import lombok.Data;

/**
 * 登录返回数据。
 */
@Data
public class LoginVo {

    private Long userId;
    private String token;
    private String role;
    private String name;
}
