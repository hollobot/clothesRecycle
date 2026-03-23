package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.SignRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到记录数据访问层。
 */
@Mapper
public interface SignRecordMapper extends BaseMapper<SignRecord> {
}
