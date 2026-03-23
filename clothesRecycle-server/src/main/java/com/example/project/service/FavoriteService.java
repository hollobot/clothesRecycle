package com.example.project.service;

import com.example.project.model.po.Item;

import java.util.List;

/**
 * 收藏业务接口。
 */
public interface FavoriteService {

    void addFavorite(Long userId, Long itemId);

    void removeFavorite(Long userId, Long itemId);

    List<Item> listMyFavorites(Long userId);
}
