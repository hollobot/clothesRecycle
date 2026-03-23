package com.example.project.service;

import com.example.project.job.OrderTimeoutJob;
import com.example.project.mapper.ItemMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.po.Item;
import com.example.project.model.po.Order;
import com.example.project.model.vo.order.OrderListVo;
import com.example.project.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单核心流程回归测试。
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private PointService pointService;
    @Mock
    private MessageService messageService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private OrderTimeoutJob orderTimeoutJob;
    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void should_restore_item_status_when_cancel_order_and_no_active_orders() {
        Order order = new Order();
        order.setId(100L);
        order.setItemId(10L);
        order.setBuyerId(2L);
        order.setSellerId(1L);
        order.setStatus("PENDING_CONFIRM");
        order.setAcquireType("FREE");
        order.setPointAmount(0);

        Item item = new Item();
        item.setId(10L);
        item.setStatus("TRADING");

        when(orderMapper.selectById(100L)).thenReturn(order);
        when(orderMapper.selectCount(any())).thenReturn(0L);
        when(itemMapper.selectById(10L)).thenReturn(item);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        orderService.cancelOrder(1L, 100L);

        verify(itemMapper).updateById(item);
        assertThat(item.getStatus()).isEqualTo("ON_SHELF");
    }

    @Test
    void should_return_action_flags_and_item_info_when_list_my_orders() {
        Order order = new Order();
        order.setId(200L);
        order.setItemId(10L);
        order.setBuyerId(2L);
        order.setSellerId(1L);
        order.setStatus("PENDING_CONFIRM");
        order.setAcquireType("FREE");

        Item item = new Item();
        item.setId(10L);
        item.setTitle("测试卫衣");
        item.setCoverUrl("http://img/cover.jpg");
        item.setStatus("TRADING");

        when(orderMapper.selectList(any())).thenReturn(List.of(order));
        when(itemMapper.selectById(10L)).thenReturn(item);

        List<OrderListVo> result = orderService.listMyOrders(1L);

        assertThat(result).hasSize(1);
        OrderListVo vo = result.get(0);
        assertThat(vo.getItemTitle()).isEqualTo("测试卫衣");
        assertThat(vo.getItemCoverUrl()).isEqualTo("http://img/cover.jpg");
        assertThat(vo.getCanConfirm()).isTrue();
        assertThat(vo.getCanCancel()).isTrue();
        assertThat(vo.getCanComplete()).isFalse();
    }
}
