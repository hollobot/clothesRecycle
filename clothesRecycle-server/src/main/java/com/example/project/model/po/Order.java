package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 认领订单实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order")
public class Order extends BaseEntity {

    private Long itemId;
    private Long buyerId;
    private Long sellerId;
    private Long campusId;
    private String acquireType;
    private Integer pointAmount;
    private String status;
    private String remark;
}
