package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.GiftExchangeMapper;
import com.example.project.mapper.GiftMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.model.dto.gift.SaveGiftDto;
import com.example.project.model.po.Gift;
import com.example.project.model.po.GiftExchange;
import com.example.project.model.po.User;
import com.example.project.service.GiftService;
import com.example.project.service.PointService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 礼品业务实现。
 */
@Service
public class GiftServiceImpl implements GiftService {

    private static final DateTimeFormatter CODE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    private final GiftMapper giftMapper;
    private final GiftExchangeMapper giftExchangeMapper;
    private final UserMapper userMapper;
    private final PointService pointService;

    public GiftServiceImpl(GiftMapper giftMapper,
                           GiftExchangeMapper giftExchangeMapper,
                           UserMapper userMapper,
                           PointService pointService) {
        this.giftMapper = giftMapper;
        this.giftExchangeMapper = giftExchangeMapper;
        this.userMapper = userMapper;
        this.pointService = pointService;
    }

    @Override
    public List<Gift> listPublicGifts() {
        LambdaQueryWrapper<Gift> query = new LambdaQueryWrapper<Gift>()
                .eq(Gift::getStatus, 1)
                .orderByDesc(Gift::getCreateTime);
        return giftMapper.selectList(query);
    }

    @Override
    public List<Gift> listAdminGifts() {
        LambdaQueryWrapper<Gift> query = new LambdaQueryWrapper<Gift>()
                .orderByDesc(Gift::getCreateTime);
        return giftMapper.selectList(query);
    }

    @Override
    public Long createGift(SaveGiftDto dto) {
        Gift gift = new Gift();
        BeanUtils.copyProperties(dto, gift);
        gift.setImageUrl(dto.getImageUrl() == null ? "" : dto.getImageUrl());
        gift.setStatus(1);
        gift.setExchangedCount(0);
        giftMapper.insert(gift);
        return gift.getId();
    }

    @Override
    public void updateGift(Long giftId, SaveGiftDto dto) {
        Gift gift = giftMapper.selectById(giftId);
        if (gift == null) {
            throw new BusinessException("礼品不存在");
        }
        BeanUtils.copyProperties(dto, gift);
        gift.setImageUrl(dto.getImageUrl() == null ? "" : dto.getImageUrl());
        giftMapper.updateById(gift);
    }

    @Override
    public void changeGiftStatus(Long giftId, boolean enabled) {
        Gift gift = giftMapper.selectById(giftId);
        if (gift == null) {
            throw new BusinessException("礼品不存在");
        }
        gift.setStatus(enabled ? 1 : 0);
        giftMapper.updateById(gift);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exchangeGift(Long userId, Long giftId) {
        Gift gift = giftMapper.selectById(giftId);
        if (gift == null || gift.getStatus() == null || gift.getStatus() != 1) {
            throw new BusinessException("礼品不可兑换");
        }
        if (gift.getStock() == null || gift.getStock() <= 0) {
            throw new BusinessException("礼品库存不足");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getPointBalance() < gift.getPointCost()) {
            throw new BusinessException("积分不足");
        }

        pointService.addPoint(userId, -gift.getPointCost(), "GIFT_EXCHANGE", giftId, "积分兑换礼品");

        gift.setStock(gift.getStock() - 1);
        gift.setExchangedCount(gift.getExchangedCount() + 1);
        giftMapper.updateById(gift);

        GiftExchange exchange = new GiftExchange();
        exchange.setGiftId(giftId);
        exchange.setUserId(userId);
        exchange.setPointCost(gift.getPointCost());
        exchange.setExchangeCode(generateExchangeCode(userId));
        exchange.setStatus("PENDING");
        giftExchangeMapper.insert(exchange);
        return exchange.getExchangeCode();
    }

    @Override
    public List<GiftExchange> listMyExchanges(Long userId) {
        LambdaQueryWrapper<GiftExchange> query = new LambdaQueryWrapper<GiftExchange>()
                .eq(GiftExchange::getUserId, userId)
                .orderByDesc(GiftExchange::getCreateTime);
        return giftExchangeMapper.selectList(query);
    }

    @Override
    public List<GiftExchange> listAllExchanges() {
        LambdaQueryWrapper<GiftExchange> query = new LambdaQueryWrapper<GiftExchange>()
                .orderByDesc(GiftExchange::getCreateTime);
        return giftExchangeMapper.selectList(query);
    }

    @Override
    public void verifyExchange(Long exchangeId) {
        GiftExchange exchange = giftExchangeMapper.selectById(exchangeId);
        if (exchange == null) {
            throw new BusinessException("兑换记录不存在");
        }
        if (!"PENDING".equals(exchange.getStatus())) {
            throw new BusinessException("当前状态不可核销");
        }
        exchange.setStatus("VERIFIED");
        exchange.setVerifyTime(LocalDateTime.now());
        giftExchangeMapper.updateById(exchange);
    }

    private String generateExchangeCode(Long userId) {
        String time = LocalDateTime.now().format(CODE_TIME_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "EX" + time + userId + random;
    }
}
