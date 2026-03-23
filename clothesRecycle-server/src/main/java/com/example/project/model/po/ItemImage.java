package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品图片实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("item_image")
public class ItemImage extends BaseEntity {

    private Long itemId;
    private String imageUrl;
    private Integer sortNo;
}
