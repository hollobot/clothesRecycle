package com.example.project.service;

/**
 * 积分业务接口。
 */
public interface PointService {

    void addPoint(Long userId, int amount, String bizType, Long bizId, String remark);

    void freezePoint(Long userId, int amount, String bizType, Long bizId, String remark);

    void unfreezePoint(Long userId, int amount, String bizType, Long bizId, String remark);

    void transferFrozenPoint(Long fromUserId, Long toUserId, int amount, String bizType, Long bizId, String remark);
}
