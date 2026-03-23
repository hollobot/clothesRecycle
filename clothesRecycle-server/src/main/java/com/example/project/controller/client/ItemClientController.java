package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
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
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        return Result.ok(itemService.publish(userId, dto));
    }

    /**
     * 公开物品列表。
     */
    @GetMapping("/api/public/items")
    public Result<List<Item>> listPublic(@RequestParam(required = false) Long campusId) {
        return Result.ok(itemService.listPublicItems(campusId));
    }

    /**
     * 物品详情。
     */
    @GetMapping("/api/public/items/{itemId}")
    public Result<ItemDetailVo> detail(@PathVariable Long itemId) {
        return Result.ok(itemService.getDetail(itemId));
    }
}
