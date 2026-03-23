package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.gift.SaveGiftDto;
import com.example.project.model.po.Gift;
import com.example.project.model.po.GiftExchange;
import com.example.project.service.GiftService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端礼品控制器。
 */
@RestController
@RequestMapping("/api/admin/gifts")
public class GiftAdminController {

    private final GiftService giftService;

    public GiftAdminController(GiftService giftService) {
        this.giftService = giftService;
    }

    /**
     * 礼品列表。
     */
    @GetMapping
    public Result<List<Gift>> list() {
        return Result.ok(giftService.listAdminGifts());
    }

    /**
     * 新增礼品。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SaveGiftDto dto) {
        return Result.ok(giftService.createGift(dto));
    }

    /**
     * 编辑礼品。
     */
    @PutMapping("/{giftId}")
    public Result<Void> update(@PathVariable Long giftId, @Valid @RequestBody SaveGiftDto dto) {
        giftService.updateGift(giftId, dto);
        return Result.ok();
    }

    /**
     * 上架或下架礼品。
     */
    @PostMapping("/{giftId}/status")
    public Result<Void> changeStatus(@PathVariable Long giftId, @RequestParam boolean enabled) {
        giftService.changeGiftStatus(giftId, enabled);
        return Result.ok();
    }

    /**
     * 查询兑换记录。
     */
    @GetMapping("/exchanges")
    public Result<List<GiftExchange>> listExchanges() {
        return Result.ok(giftService.listAllExchanges());
    }

    /**
     * 核销兑换。
     */
    @PostMapping("/exchanges/{exchangeId}/verify")
    public Result<Void> verify(@PathVariable Long exchangeId) {
        giftService.verifyExchange(exchangeId);
        return Result.ok();
    }
}
