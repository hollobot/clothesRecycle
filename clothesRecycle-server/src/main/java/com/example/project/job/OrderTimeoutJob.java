package com.example.project.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.po.Order;
import com.example.project.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 订单超时处理任务。
 */
@Component
public class OrderTimeoutJob {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutJob.class);
    private static final String ORDER_TIMEOUT_KEY = "order:timeout:zset";

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderMapper orderMapper;
    private final PointService pointService;

    public OrderTimeoutJob(RedisTemplate<String, Object> redisTemplate,
                           OrderMapper orderMapper,
                           PointService pointService) {
        this.redisTemplate = redisTemplate;
        this.orderMapper = orderMapper;
        this.pointService = pointService;
    }

    /**
     * 每分钟扫描一次超时订单。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void handleTimeoutOrders() {
        long now = System.currentTimeMillis();
        Set<Object> orderIds = redisTemplate.opsForZSet().rangeByScore(ORDER_TIMEOUT_KEY, 0, now);
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }

        for (Object orderIdObj : orderIds) {
            Long orderId = Long.valueOf(String.valueOf(orderIdObj));
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                redisTemplate.opsForZSet().remove(ORDER_TIMEOUT_KEY, orderIdObj);
                continue;
            }

            if ("PENDING_CONFIRM".equals(order.getStatus()) || "PENDING_DELIVERY".equals(order.getStatus())) {
                order.setStatus("CANCELLED");
                orderMapper.updateById(order);
                if ("POINT".equals(order.getAcquireType()) && order.getPointAmount() > 0) {
                    pointService.unfreezePoint(order.getBuyerId(), order.getPointAmount(), "ORDER_TIMEOUT", order.getId(), "订单超时自动取消解冻");
                }
                log.info("订单超时已取消, orderId={}", orderId);
            }
            redisTemplate.opsForZSet().remove(ORDER_TIMEOUT_KEY, orderIdObj);
        }
    }

    /**
     * 写入超时任务。
     */
    public void addTimeoutTask(Long orderId, long timeoutTimestamp) {
        redisTemplate.opsForZSet().add(ORDER_TIMEOUT_KEY, String.valueOf(orderId), timeoutTimestamp);
    }
}
