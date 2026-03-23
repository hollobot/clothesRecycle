package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.FavoriteMapper;
import com.example.project.mapper.ItemMapper;
import com.example.project.model.po.Favorite;
import com.example.project.model.po.Item;
import com.example.project.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏业务实现。
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ItemMapper itemMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ItemMapper itemMapper) {
        this.favoriteMapper = favoriteMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public void addFavorite(Long userId, Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemId, itemId);
        if (favoriteMapper.selectCount(query) > 0) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemId(itemId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long itemId) {
        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemId, itemId);
        favoriteMapper.delete(query);
    }

    @Override
    public List<Item> listMyFavorites(Long userId) {
        LambdaQueryWrapper<Favorite> query = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(query);

        List<Item> items = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Item item = itemMapper.selectById(favorite.getItemId());
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
}
