package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校区实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus")
public class Campus extends BaseEntity {

    private String name;
    private String address;
    private Integer status;
    private String remark;
}
