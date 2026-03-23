package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物品数据访问层。
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}
