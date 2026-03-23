package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分流水实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_record")
public class PointRecord extends BaseEntity {

    private Long userId;
    private Integer changeAmount;
    private String bizType;
    private Long bizId;
    private String remark;
}
