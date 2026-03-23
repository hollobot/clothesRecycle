package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生用户实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    private String phone;
    private String password;
    private String studentId;
    private String name;
    private Long campusId;
    private String avatarUrl;
    private Integer pointBalance;
    private Integer frozenPoint;
    private Integer status;
}
