package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.model.po.Gift;
import com.example.project.model.po.GiftExchange;
import com.example.project.service.GiftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端礼品控制器。
 */
@RestController
public class GiftClientController {

    private final GiftService giftService;

    public GiftClientController(GiftService giftService) {
        this.giftService = giftService;
    }

    /**
     * 礼品商城列表。
     */
    @GetMapping("/api/public/gifts")
    public Result<List<Gift>> listPublicGifts() {
        return Result.ok(giftService.listPublicGifts());
    }

    /**
     * 兑换礼品。
     */
    @PostMapping("/api/user/gifts/{giftId}/exchange")
    public Result<Map<String, Object>> exchange(@PathVariable Long giftId) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        String exchangeCode = giftService.exchangeGift(userId, giftId);
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        return Result.ok(data);
    }

    /**
     * 我的礼品兑换记录。
     */
    @GetMapping("/api/user/gifts/exchanges")
    public Result<List<GiftExchange>> listMyExchanges() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        return Result.ok(giftService.listMyExchanges(userId));
    }
}
