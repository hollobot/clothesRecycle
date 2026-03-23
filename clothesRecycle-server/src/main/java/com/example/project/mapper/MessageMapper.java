package com.example.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.model.po.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内消息数据访问层。
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
