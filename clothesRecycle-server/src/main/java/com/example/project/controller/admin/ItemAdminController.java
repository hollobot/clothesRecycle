package com.example.project.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.common.result.Result;
import com.example.project.mapper.ItemMapper;
import com.example.project.model.po.Item;
import com.example.project.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端物品控制器。
 */
@RestController
@RequestMapping("/api/admin/items")
public class ItemAdminController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemAdminController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    /**
     * 查询物品列表。
     */
    @GetMapping
    public Result<List<Item>> list(@RequestParam(required = false) String status) {
        LambdaQueryWrapper<Item> query = new LambdaQueryWrapper<Item>()
                .eq(status != null && !status.isBlank(), Item::getStatus, status)
                .orderByDesc(Item::getCreateTime);
        return Result.ok(itemMapper.selectList(query));
    }

    /**
     * 审核物品。
     */
    @PostMapping("/{itemId}/audit")
    public Result<Void> audit(@PathVariable Long itemId,
                              @RequestParam boolean approved,
                              @RequestParam(required = false) String reason) {
        Long adminId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        itemService.audit(itemId, approved, reason, adminId);
        return Result.ok();
    }

    /**
     * 强制下架。
     */
    @PostMapping("/{itemId}/force-off-shelf")
    public Result<Void> forceOffShelf(@PathVariable Long itemId,
                                      @RequestParam(required = false) String reason) {
        Long adminId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        itemService.forceOffShelf(itemId, reason, adminId);
        return Result.ok();
    }
}
