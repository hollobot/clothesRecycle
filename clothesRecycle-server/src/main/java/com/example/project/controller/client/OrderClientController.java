package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.exception.BusinessException;
import com.example.project.model.dto.order.CreateOrderDto;
import com.example.project.model.vo.order.OrderListVo;
import com.example.project.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端订单控制器。
 */
@RestController
@RequestMapping("/api/user/orders")
public class OrderClientController {

    private final OrderService orderService;

    public OrderClientController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateOrderDto dto) {
        Long userId = getCurrentUserId();
        return Result.ok(orderService.createOrder(userId, dto));
    }

    /**
     * 发布方确认订单。
     */
    @PostMapping("/{orderId}/confirm")
    public Result<Void> confirm(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        orderService.confirmOrder(userId, orderId);
        return Result.ok();
    }

    /**
     * 取消订单。
     */
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(userId, orderId);
        return Result.ok();
    }

    /**
     * 完成订单。
     */
    @PostMapping("/{orderId}/complete")
    public Result<Void> complete(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        orderService.completeOrder(userId, orderId);
        return Result.ok();
    }

    /**
     * 查询我的订单。
     */
    @GetMapping
    public Result<List<OrderListVo>> listMyOrders() {
        Long userId = getCurrentUserId();
        return Result.ok(orderService.listMyOrders(userId));
    }

    /**
     * 解析当前登录用户 ID，兼容 Sa-Token 登录态对象与字符串类型。
     */
    private Long getCurrentUserId() {
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException("用户登录态无效，请重新登录");
        }
    }
}
