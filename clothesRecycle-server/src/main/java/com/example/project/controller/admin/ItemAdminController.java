package com.example.project.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.common.result.Result;
import com.example.project.mapper.ItemMapper;
import com.example.project.model.po.Admin;
import com.example.project.model.po.Item;
import com.example.project.model.vo.item.ItemDetailVo;
import com.example.project.service.ItemService;
import com.example.project.service.support.AdminSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端物品控制器。
 */
@RestController
@RequestMapping("/api/admin/items")
public class ItemAdminController {

    private final ItemService itemService;
    /**
     * 物品数据访问。
     */
    private final ItemMapper itemMapper;
    /**
     * 管理端会话与权限范围服务。
     */
    private final AdminSessionService adminSessionService;

    public ItemAdminController(ItemService itemService,
                               ItemMapper itemMapper,
                               AdminSessionService adminSessionService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.adminSessionService = adminSessionService;
    }

    /**
     * 查询物品列表。
     */
    @GetMapping
    public Result<List<Item>> list(@RequestParam(required = false) String status) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);

        LambdaQueryWrapper<Item> query = new LambdaQueryWrapper<Item>()
                .eq(status != null && !status.isBlank(), Item::getStatus, status)
                .eq(scopedCampusId != null, Item::getCampusId, scopedCampusId)
                .orderByDesc(Item::getCreateTime);
        return Result.ok(itemMapper.selectList(query));
    }

    /**
     * 查询物品审核详情（含图片列表）。
     */
    @GetMapping("/{itemId}")
    public Result<ItemDetailVo> detail(@PathVariable Long itemId) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Item item = itemMapper.selectById(itemId);
        if (item != null) {
            adminSessionService.requireCampusAccess(currentAdmin, item.getCampusId(), "查看");
        }
        return Result.ok(itemService.getDetail(itemId));
    }

    /**
     * 审核物品。
     */
    @PostMapping("/{itemId}/audit")
    public Result<Void> audit(@PathVariable Long itemId,
                              @RequestParam boolean approved,
                              @RequestParam(required = false) String reason) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long operatorCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        itemService.audit(itemId, approved, reason, currentAdmin.getId(), operatorCampusId);
        return Result.ok();
    }

    /**
     * 强制下架。
     */
    @PostMapping("/{itemId}/force-off-shelf")
    public Result<Void> forceOffShelf(@PathVariable Long itemId,
                                      @RequestParam(required = false) String reason) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long operatorCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        itemService.forceOffShelf(itemId, reason, currentAdmin.getId(), operatorCampusId);
        return Result.ok();
    }
}
