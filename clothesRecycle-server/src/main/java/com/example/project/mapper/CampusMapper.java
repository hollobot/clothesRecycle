package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.Campus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校区数据访问层。
 */
@Mapper
public interface CampusMapper extends BaseMapper<Campus> {
}
