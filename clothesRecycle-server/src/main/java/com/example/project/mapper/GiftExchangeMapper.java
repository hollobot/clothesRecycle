package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.GiftExchange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 礼品兑换记录数据访问层。
 */
@Mapper
public interface GiftExchangeMapper extends BaseMapper<GiftExchange> {
}
