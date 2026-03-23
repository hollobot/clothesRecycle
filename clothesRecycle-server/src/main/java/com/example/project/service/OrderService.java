package com.example.project.service;

import com.example.project.model.dto.order.CreateOrderDto;
import com.example.project.model.po.Order;

import java.util.List;

/**
 * 订单业务接口。
 */
public interface OrderService {

    Long createOrder(Long buyerId, CreateOrderDto dto);

    void confirmOrder(Long sellerId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    void completeOrder(Long userId, Long orderId);

    List<Order> listMyOrders(Long userId);
}
