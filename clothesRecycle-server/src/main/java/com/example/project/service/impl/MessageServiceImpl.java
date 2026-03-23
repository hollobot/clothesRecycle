package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.MessageMapper;
import com.example.project.model.po.Message;
import com.example.project.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站内消息业务实现。
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public void sendMessage(Long userId, String title, String content, String type) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setMsgType(type);
        message.setReadStatus(0);
        messageMapper.insert(message);
    }

    @Override
    public Page<Message> pageUserMessages(Long userId, long pageNum, long pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(page, query);
    }

    @Override
    public long countUnread(Long userId) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getReadStatus, 0);
        return messageMapper.selectCount(query);
    }

    @Override
    public void markRead(Long userId, Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || !userId.equals(message.getUserId())) {
            throw new BusinessException("消息不存在");
        }
        if (message.getReadStatus() != null && message.getReadStatus() == 1) {
            return;
        }
        message.setReadStatus(1);
        messageMapper.updateById(message);
    }

    @Override
    public void markAllRead(Long userId) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getReadStatus, 0);
        List<Message> messages = messageMapper.selectList(query);
        for (Message message : messages) {
            message.setReadStatus(1);
            messageMapper.updateById(message);
        }
    }
}
