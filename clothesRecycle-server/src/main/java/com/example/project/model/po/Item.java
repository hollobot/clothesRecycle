package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("item")
public class Item extends BaseEntity {

    private Long userId;
    private Long campusId;
    private String title;
    private String category;
    private String genderType;
    private String sizeType;
    private String conditionLevel;
    private String description;
    private String coverUrl;
    private String acquireType;
    private Integer pointPrice;
    private String status;
    private String auditReason;
}
