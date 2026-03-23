package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.ItemImageMapper;
import com.example.project.mapper.ItemMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.dto.item.PublishItemDto;
import com.example.project.model.po.Item;
import com.example.project.model.po.ItemImage;
import com.example.project.model.po.Order;
import com.example.project.model.vo.item.ItemDetailVo;
import com.example.project.service.ItemService;
import com.example.project.service.MessageService;
import com.example.project.service.PointService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物品业务实现。
 */
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemImageMapper itemImageMapper;
    private final OrderMapper orderMapper;
    private final PointService pointService;
    private final MessageService messageService;

    public ItemServiceImpl(ItemMapper itemMapper,
                           ItemImageMapper itemImageMapper,
                           OrderMapper orderMapper,
                           PointService pointService,
                           MessageService messageService) {
        this.itemMapper = itemMapper;
        this.itemImageMapper = itemImageMapper;
        this.orderMapper = orderMapper;
        this.pointService = pointService;
        this.messageService = messageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publish(Long userId, PublishItemDto dto) {
        validateImageUrls(dto.getImageUrls());

        Item item = new Item();
        BeanUtils.copyProperties(dto, item);
        item.setUserId(userId);
        // 发布时将第一张图作为封面，保证列表页有稳定展示图。
        item.setCoverUrl(dto.getImageUrls().get(0));
        item.setPointPrice(dto.getPointPrice() == null ? 0 : dto.getPointPrice());
        item.setStatus("PENDING_AUDIT");
        itemMapper.insert(item);

        for (int i = 0; i < dto.getImageUrls().size(); i++) {
            ItemImage itemImage = new ItemImage();
            itemImage.setItemId(item.getId());
            itemImage.setImageUrl(dto.getImageUrls().get(i));
            itemImage.setSortNo(i);
            itemImageMapper.insert(itemImage);
        }

        return item.getId();
    }

    @Override
    public void audit(Long itemId, boolean approved, String reason, Long adminId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!"PENDING_AUDIT".equals(item.getStatus())) {
            throw new BusinessException("当前状态不可审核");
        }
        item.setStatus(approved ? "ON_SHELF" : "REJECTED");
        item.setAuditReason(reason == null ? "" : reason);
        itemMapper.updateById(item);
        if (approved) {
            pointService.addPoint(item.getUserId(), 30, "ITEM_AUDIT_PASS", item.getId(), "物品审核通过奖励");
        }
        messageService.sendMessage(item.getUserId(), "物品审核结果", approved ? "审核已通过" : "审核已拒绝: " + reason, "AUDIT");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceOffShelf(Long itemId, String reason, Long adminId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        item.setStatus("FORCE_OFF_SHELF");
        item.setAuditReason(reason == null ? "管理员强制下架" : reason);
        itemMapper.updateById(item);

        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<Order>()
                .eq(Order::getItemId, itemId)
                .in(Order::getStatus, "PENDING_CONFIRM", "PENDING_DELIVERY");
        List<Order> orders = orderMapper.selectList(orderQuery);
        for (Order order : orders) {
            order.setStatus("CANCELLED");
            orderMapper.updateById(order);
            if ("POINT".equals(order.getAcquireType()) && order.getPointAmount() > 0) {
                pointService.unfreezePoint(order.getBuyerId(), order.getPointAmount(), "ORDER_CANCEL", order.getId(), "强制下架订单解冻");
            }
            messageService.sendMessage(order.getBuyerId(), "订单已取消", "管理员强制下架，订单自动取消", "ORDER");
            messageService.sendMessage(order.getSellerId(), "订单已取消", "管理员强制下架，订单自动取消", "ORDER");
        }
        messageService.sendMessage(item.getUserId(), "物品被下架", "管理员已下架你的物品: " + item.getTitle(), "ITEM");
    }

    @Override
    public ItemDetailVo getDetail(Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        LambdaQueryWrapper<ItemImage> imageQuery = new LambdaQueryWrapper<ItemImage>()
                .eq(ItemImage::getItemId, itemId)
                .orderByAsc(ItemImage::getSortNo)
                .orderByAsc(ItemImage::getId);
        List<String> imageUrls = itemImageMapper.selectList(imageQuery).stream()
                .map(ItemImage::getImageUrl)
                .toList();
        if (imageUrls.isEmpty() && item.getCoverUrl() != null && !item.getCoverUrl().isBlank()) {
            imageUrls = List.of(item.getCoverUrl());
        }

        ItemDetailVo vo = new ItemDetailVo();
        BeanUtils.copyProperties(item, vo);
        vo.setImageUrls(imageUrls);
        return vo;
    }

    @Override
    public List<Item> listMyItems(Long userId) {
        LambdaQueryWrapper<Item> query = new LambdaQueryWrapper<Item>()
                .eq(Item::getUserId, userId)
                .orderByDesc(Item::getCreateTime);
        return itemMapper.selectList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMyItem(Long userId, Long itemId, String reason) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!userId.equals(item.getUserId())) {
            throw new BusinessException("无权限操作该物品");
        }
        if ("TRADING".equals(item.getStatus()) || "DONE".equals(item.getStatus())) {
            throw new BusinessException("当前状态不可取消发布");
        }
        if ("FORCE_OFF_SHELF".equals(item.getStatus())) {
            throw new BusinessException("该物品已被管理员下架");
        }

        // 用户主动取消发布统一落 OFF_SHELF，便于客户端展示状态轨迹。
        item.setStatus("OFF_SHELF");
        item.setAuditReason(reason == null || reason.isBlank() ? "用户主动取消发布" : reason);
        itemMapper.updateById(item);
    }

    @Override
    public List<Item> listPublicItems(Long campusId, String keyword, String category) {
        String trimmedKeyword = keyword == null ? null : keyword.trim();
        String trimmedCategory = category == null ? null : category.trim();

        LambdaQueryWrapper<Item> query = new LambdaQueryWrapper<Item>()
                .eq(campusId != null, Item::getCampusId, campusId)
                .and(trimmedKeyword != null && !trimmedKeyword.isBlank(),
                        wrapper -> wrapper.like(Item::getTitle, trimmedKeyword)
                                .or()
                                .like(Item::getDescription, trimmedKeyword))
                .eq(trimmedCategory != null && !trimmedCategory.isBlank(), Item::getCategory, trimmedCategory)
                .eq(Item::getStatus, "ON_SHELF")
                .orderByDesc(Item::getCreateTime);
        return itemMapper.selectList(query);
    }

    private void validateImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new BusinessException("请至少上传一张物品图片");
        }
        if (imageUrls.size() > 9) {
            throw new BusinessException("最多上传9张物品图片");
        }
        for (String imageUrl : imageUrls) {
            if (imageUrl == null || imageUrl.isBlank()) {
                throw new BusinessException("图片地址不能为空");
            }
            if (imageUrl.length() > 255) {
                throw new BusinessException("图片地址长度不能超过255");
            }
        }
    }
}
