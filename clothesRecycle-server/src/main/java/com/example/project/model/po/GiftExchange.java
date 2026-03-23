package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 礼品兑换记录实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gift_exchange")
public class GiftExchange extends BaseEntity {

    private Long giftId;
    private Long userId;
    private Integer pointCost;
    private String exchangeCode;
    private String status;
    private LocalDateTime verifyTime;
}
