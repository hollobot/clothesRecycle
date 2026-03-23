package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.PointRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分流水数据访问层。
 */
@Mapper
public interface PointRecordMapper extends BaseMapper<PointRecord> {
}
