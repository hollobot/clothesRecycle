package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.model.po.Item;
import com.example.project.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端收藏控制器。
 */
@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteClientController {

    private final FavoriteService favoriteService;

    public FavoriteClientController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏物品。
     */
    @PostMapping("/{itemId}")
    public Result<Void> add(@PathVariable Long itemId) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        favoriteService.addFavorite(userId, itemId);
        return Result.ok();
    }

    /**
     * 取消收藏。
     */
    @DeleteMapping("/{itemId}")
    public Result<Void> remove(@PathVariable Long itemId) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        favoriteService.removeFavorite(userId, itemId);
        return Result.ok();
    }

    /**
     * 我的收藏列表。
     */
    @GetMapping
    public Result<List<Item>> list() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        return Result.ok(favoriteService.listMyFavorites(userId));
    }
}
