package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 认领订单实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
/**
 * order 是 MySQL 保留关键字，表名需要加反引号避免 SQL 语法错误。
 */
@TableName("`order`")
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
