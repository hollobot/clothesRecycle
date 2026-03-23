package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分礼品实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gift")
public class Gift extends BaseEntity {

    private String name;
    private String description;
    private String imageUrl;
    private Integer pointCost;
    private Integer stock;
    private Integer exchangedCount;
    private Integer status;
}
