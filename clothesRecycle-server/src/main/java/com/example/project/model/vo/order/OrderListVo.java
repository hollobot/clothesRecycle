package com.example.project.model.vo.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单列表展示对象。
 */
@Data
public class OrderListVo {

    /**
     * 订单 ID。
     */
    private Long id;
    /**
     * 物品 ID。
     */
    private Long itemId;
    /**
     * 物品标题。
     */
    private String itemTitle;
    /**
     * 物品封面图。
     */
    private String itemCoverUrl;
    /**
     * 当前物品状态。
     */
    private String itemStatus;
    /**
     * 领取方 ID。
     */
    private Long buyerId;
    /**
     * 发布方 ID。
     */
    private Long sellerId;
    /**
     * 获取方式：FREE / POINT。
     */
    private String acquireType;
    /**
     * 积分金额。
     */
    private Integer pointAmount;
    /**
     * 订单状态。
     */
    private String status;
    /**
     * 订单备注。
     */
    private String remark;
    /**
     * 创建时间。
     */
    private LocalDateTime createTime;
    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;

    /**
     * 当前登录用户是否可确认（同意申请）。
     */
    private Boolean canConfirm;
    /**
     * 当前登录用户是否可取消。
     */
    private Boolean canCancel;
    /**
     * 当前登录用户是否可确认完成。
     */
    private Boolean canComplete;
}
