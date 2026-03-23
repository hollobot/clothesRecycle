package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.ItemImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物品图片数据访问层。
 */
@Mapper
public interface ItemImageMapper extends BaseMapper<ItemImage> {
}
