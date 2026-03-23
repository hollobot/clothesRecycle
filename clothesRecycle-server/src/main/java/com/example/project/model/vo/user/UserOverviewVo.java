package com.example.project.model.vo.user;

import lombok.Data;

/**
 * 用户个人中心概览数据。
 */
@Data
public class UserOverviewVo {

    /**
     * 已完成捐赠数量（发布方视角）。
     */
    private Long donateCount;
    /**
     * 已完成领取数量（领取方视角）。
     */
    private Long claimCount;
    /**
     * 当前积分余额。
     */
    private Integer pointBalance;
}
