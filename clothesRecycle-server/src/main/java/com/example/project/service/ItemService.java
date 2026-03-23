package com.example.project.service;

import com.example.project.model.dto.item.PublishItemDto;
import com.example.project.model.po.Item;
import com.example.project.model.vo.item.ItemDetailVo;

import java.util.List;

/**
 * 物品业务接口。
 */
public interface ItemService {

    Long publish(Long userId, PublishItemDto dto);

    void audit(Long itemId, boolean approved, String reason, Long adminId);

    void forceOffShelf(Long itemId, String reason, Long adminId);

    ItemDetailVo getDetail(Long itemId);

    List<Item> listPublicItems(Long campusId);
}
