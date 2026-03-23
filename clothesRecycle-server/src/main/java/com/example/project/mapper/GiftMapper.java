package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.Gift;
import org.apache.ibatis.annotations.Mapper;

/**
 * 礼品数据访问层。
 */
@Mapper
public interface GiftMapper extends BaseMapper<Gift> {
}
