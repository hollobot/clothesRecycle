package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.exception.BusinessException;
import com.example.project.model.dto.item.PublishItemDto;
import com.example.project.model.po.Item;
import com.example.project.model.vo.item.ItemDetailVo;
import com.example.project.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端物品控制器。
 */
@RestController
public class ItemClientController {

    private final ItemService itemService;

    public ItemClientController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 用户发布物品。
     */
    @PostMapping("/api/user/items")
    public Result<Long> publish(@Valid @RequestBody PublishItemDto dto) {
        Long userId = getCurrentUserId();
        return Result.ok(itemService.publish(userId, dto));
    }

    /**
     * 当前用户发布的物品列表。
     */
    @GetMapping("/api/user/items/mine")
    public Result<List<Item>> listMine() {
        return Result.ok(itemService.listMyItems(getCurrentUserId()));
    }

    /**
     * 用户主动取消发布。
     */
    @PostMapping("/api/user/items/{itemId}/cancel")
    public Result<Void> cancelMine(@PathVariable Long itemId,
                                   @RequestParam(required = false) String reason) {
        itemService.cancelMyItem(getCurrentUserId(), itemId, reason);
        return Result.ok();
    }

    /**
     * 公开物品列表。
     */
    @GetMapping("/api/public/items")
    public Result<List<Item>> listPublic(
            @RequestParam(required = false) Long campusId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        return Result.ok(itemService.listPublicItems(campusId, keyword, category));
    }

    /**
     * 物品详情。
     */
    @GetMapping("/api/public/items/{itemId}")
    public Result<ItemDetailVo> detail(@PathVariable Long itemId) {
        return Result.ok(itemService.getDetail(itemId));
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
