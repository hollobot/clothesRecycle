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

    /**
     * 查询当前用户发布的物品列表。
     */
    List<Item> listMyItems(Long userId);

    /**
     * 用户主动取消发布。
     */
    void cancelMyItem(Long userId, Long itemId, String reason);

    /**
     * 查询公开物品列表。
     *
     * @param campusId 校区 ID（可选）
     * @param keyword  关键词，匹配标题与描述（可选）
     * @param category 分类（可选）
     * @return 公开可浏览的物品列表
     */
    List<Item> listPublicItems(Long campusId, String keyword, String category);
}
