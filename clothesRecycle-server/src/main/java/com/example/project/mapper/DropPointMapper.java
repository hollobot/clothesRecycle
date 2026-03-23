package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.DropPoint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回收点数据访问层。
 */
@Mapper
public interface DropPointMapper extends BaseMapper<DropPoint> {
}
