package com.example.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.model.po.Message;

/**
 * 消息业务接口。
 */
public interface MessageService {

    void sendMessage(Long userId, String title, String content, String type);

    Page<Message> pageUserMessages(Long userId, long pageNum, long pageSize);

    long countUnread(Long userId);

    void markRead(Long userId, Long messageId);

    void markAllRead(Long userId);
}
