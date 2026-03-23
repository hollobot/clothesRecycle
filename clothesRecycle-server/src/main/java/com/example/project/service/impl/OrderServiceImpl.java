package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.job.OrderTimeoutJob;
import com.example.project.mapper.ItemMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.dto.order.CreateOrderDto;
import com.example.project.model.po.Item;
import com.example.project.model.po.Order;
import com.example.project.service.MessageService;
import com.example.project.service.OrderService;
import com.example.project.service.PointService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单业务实现。
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final PointService pointService;
    private final MessageService messageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderTimeoutJob orderTimeoutJob;

    public OrderServiceImpl(OrderMapper orderMapper,
                            ItemMapper itemMapper,
                            PointService pointService,
                            MessageService messageService,
                            RedisTemplate<String, Object> redisTemplate,
                            OrderTimeoutJob orderTimeoutJob) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.pointService = pointService;
        this.messageService = messageService;
        this.redisTemplate = redisTemplate;
        this.orderTimeoutJob = orderTimeoutJob;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long buyerId, CreateOrderDto dto) {
        Item item = itemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!"ON_SHELF".equals(item.getStatus())) {
            throw new BusinessException("当前物品不可认领");
        }
        if (buyerId.equals(item.getUserId())) {
            throw new BusinessException("不能认领自己发布的物品");
        }

        Order order = new Order();
        order.setItemId(item.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(item.getUserId());
        order.setCampusId(item.getCampusId());
        order.setAcquireType("POINT".equals(item.getAcquireType()) ? "POINT" : "FREE");
        order.setPointAmount("POINT".equals(item.getAcquireType()) ? item.getPointPrice() : 0);
        order.setStatus("PENDING_CONFIRM");
        order.setRemark(dto.getRemark() == null ? "" : dto.getRemark());
        orderMapper.insert(order);

        item.setStatus("TRADING");
        itemMapper.updateById(item);

        if ("POINT".equals(order.getAcquireType()) && order.getPointAmount() > 0) {
            pointService.freezePoint(buyerId, order.getPointAmount(), "ORDER_CREATE", order.getId(), "积分兑换下单冻结");
        }
        messageService.sendMessage(item.getUserId(), "新的认领申请", "你的物品有新的认领申请", "ORDER");
        long confirmTimeout = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24);
        orderTimeoutJob.addTimeoutTask(order.getId(), confirmTimeout);
        return order.getId();
    }

    @Override
    public void confirmOrder(Long sellerId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !sellerId.equals(order.getSellerId())) {
            throw new BusinessException("订单不存在或无权限");
        }
        if (!"PENDING_CONFIRM".equals(order.getStatus())) {
            throw new BusinessException("订单状态不可确认");
        }
        order.setStatus("PENDING_DELIVERY");
        orderMapper.updateById(order);
        long deliveryTimeout = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
        orderTimeoutJob.addTimeoutTask(order.getId(), deliveryTimeout);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new BusinessException("无权限操作该订单");
        }
        if (!"PENDING_CONFIRM".equals(order.getStatus()) && !"PENDING_DELIVERY".equals(order.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        Item item = itemMapper.selectById(order.getItemId());
        if (item != null) {
            item.setStatus("ON_SHELF");
            itemMapper.updateById(item);
        }
        if ("POINT".equals(order.getAcquireType()) && order.getPointAmount() > 0) {
            pointService.unfreezePoint(order.getBuyerId(), order.getPointAmount(), "ORDER_CANCEL", order.getId(), "订单取消解冻");
        }
        redisTemplate.opsForZSet().remove("order:timeout:zset", String.valueOf(order.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new BusinessException("无权限操作该订单");
        }
        if (!"PENDING_DELIVERY".equals(order.getStatus())) {
            throw new BusinessException("当前状态不可完成");
        }
        order.setStatus("DONE");
        orderMapper.updateById(order);

        Item item = itemMapper.selectById(order.getItemId());
        if (item != null) {
            item.setStatus("DONE");
            itemMapper.updateById(item);
        }

        if ("POINT".equals(order.getAcquireType()) && order.getPointAmount() > 0) {
            pointService.transferFrozenPoint(order.getBuyerId(), order.getSellerId(), order.getPointAmount(), "ORDER_DONE", order.getId(), "订单完成积分结算");
        }

        pointService.addPoint(order.getSellerId(), 30, "DONATE_DONE", order.getId(), "捐赠完成奖励");
        pointService.addPoint(order.getBuyerId(), 10, "CLAIM_DONE", order.getId(), "领取完成奖励");
        redisTemplate.opsForZSet().incrementScore("rank:donate:" + order.getCampusId(), String.valueOf(order.getSellerId()), 1D);
        redisTemplate.opsForZSet().incrementScore("rank:campus:orders", String.valueOf(order.getCampusId()), 1D);
        redisTemplate.opsForZSet().remove("order:timeout:zset", String.valueOf(order.getId()));
    }

    @Override
    public List<Order> listMyOrders(Long userId) {
        LambdaQueryWrapper<Order> query = new LambdaQueryWrapper<Order>()
                .and(q -> q.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId))
                .orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(query);
    }
}
