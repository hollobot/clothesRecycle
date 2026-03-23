package com.example.project.service;

import com.example.project.model.dto.order.CreateOrderDto;
import com.example.project.model.vo.order.OrderListVo;

import java.util.List;

/**
 * 订单业务接口。
 */
public interface OrderService {

    Long createOrder(Long buyerId, CreateOrderDto dto);

    void confirmOrder(Long sellerId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    void completeOrder(Long userId, Long orderId);

    /**
     * 查询当前用户可见的订单列表（含物品信息与可操作状态）。
     */
    List<OrderListVo> listMyOrders(Long userId);
}
