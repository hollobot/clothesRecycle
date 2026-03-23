package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.PointRecordMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.PointRecord;
import com.example.project.model.po.User;
import com.example.project.service.PointService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分业务实现。
 */
@Service
public class PointServiceImpl implements PointService {

    private final UserMapper userMapper;
    private final PointRecordMapper pointRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public PointServiceImpl(UserMapper userMapper,
                            PointRecordMapper pointRecordMapper,
                            RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.pointRecordMapper = pointRecordMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoint(Long userId, int amount, String bizType, Long bizId, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPointBalance(user.getPointBalance() + amount);
        userMapper.updateById(user);
        saveRecord(userId, amount, bizType, bizId, remark);
        refreshPointRank(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezePoint(Long userId, int amount, String bizType, Long bizId, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getPointBalance() < amount) {
            throw new BusinessException("积分不足");
        }
        user.setPointBalance(user.getPointBalance() - amount);
        user.setFrozenPoint(user.getFrozenPoint() + amount);
        userMapper.updateById(user);
        saveRecord(userId, -amount, bizType, bizId, remark + "(冻结)");
        refreshPointRank(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreezePoint(Long userId, int amount, String bizType, Long bizId, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getFrozenPoint() < amount) {
            throw new BusinessException("冻结积分不足");
        }
        user.setFrozenPoint(user.getFrozenPoint() - amount);
        user.setPointBalance(user.getPointBalance() + amount);
        userMapper.updateById(user);
        saveRecord(userId, amount, bizType, bizId, remark + "(解冻)");
        refreshPointRank(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferFrozenPoint(Long fromUserId, Long toUserId, int amount, String bizType, Long bizId, String remark) {
        User from = userMapper.selectById(fromUserId);
        User to = userMapper.selectById(toUserId);
        if (from == null || to == null) {
            throw new BusinessException("用户不存在");
        }
        if (from.getFrozenPoint() < amount) {
            throw new BusinessException("冻结积分不足");
        }
        from.setFrozenPoint(from.getFrozenPoint() - amount);
        to.setPointBalance(to.getPointBalance() + amount);
        userMapper.updateById(from);
        userMapper.updateById(to);
        saveRecord(fromUserId, -amount, bizType, bizId, remark + "(冻结扣减)");
        saveRecord(toUserId, amount, bizType, bizId, remark + "(对方获得)");
        refreshPointRank(from);
        refreshPointRank(to);
    }

    /**
     * 保存积分流水。
     */
    private void saveRecord(Long userId, int amount, String bizType, Long bizId, String remark) {
        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setChangeAmount(amount);
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setRemark(remark);
        pointRecordMapper.insert(record);
    }

    /**
     * 更新积分榜缓存。
     */
    private void refreshPointRank(User user) {
        String key = "rank:points:" + user.getCampusId();
        redisTemplate.opsForZSet().add(key, String.valueOf(user.getId()), user.getPointBalance());
    }
}
