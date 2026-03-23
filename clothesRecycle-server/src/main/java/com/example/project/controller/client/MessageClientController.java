package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.constant.SystemConstant;
import com.example.project.common.result.Result;
import com.example.project.model.po.Message;
import com.example.project.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端消息控制器。
 */
@RestController
@RequestMapping("/api/user/messages")
public class MessageClientController {

    private final MessageService messageService;

    public MessageClientController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 消息分页列表。
     */
    @GetMapping
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize
    ) {
        long queryPageNum = pageNum == null || pageNum <= 0 ? SystemConstant.DEFAULT_PAGE_NUM : pageNum;
        long queryPageSize = pageSize == null || pageSize <= 0 ? 12L : pageSize;
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));

        Page<Message> page = messageService.pageUserMessages(userId, queryPageNum, queryPageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.ok(data);
    }

    /**
     * 未读消息数。
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        long count = messageService.countUnread(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("unread", count);
        return Result.ok(data);
    }

    /**
     * 标记单条消息已读。
     */
    @PostMapping("/{messageId}/read")
    public Result<Void> markRead(@PathVariable Long messageId) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        messageService.markRead(userId, messageId);
        return Result.ok();
    }

    /**
     * 全部标记已读。
     */
    @PostMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        messageService.markAllRead(userId);
        return Result.ok();
    }
}
