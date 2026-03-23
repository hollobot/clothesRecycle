package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.common.result.Result;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.OrderMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.Order;
import com.example.project.model.po.User;
import com.example.project.model.vo.user.UserOverviewVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心信息控制器。
 */
@RestController
public class UserClientController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public UserClientController(UserMapper userMapper, OrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * 个人中心统计概览。
     */
    @GetMapping("/api/user/profile/overview")
    public Result<UserOverviewVo> overview() {
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Long donateCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getSellerId, userId)
                .eq(Order::getStatus, "DONE"));
        Long claimCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, userId)
                .eq(Order::getStatus, "DONE"));

        UserOverviewVo vo = new UserOverviewVo();
        vo.setDonateCount(donateCount == null ? 0L : donateCount);
        vo.setClaimCount(claimCount == null ? 0L : claimCount);
        vo.setPointBalance(user.getPointBalance() == null ? 0 : user.getPointBalance());
        return Result.ok(vo);
    }

    /**
     * 解析当前登录用户 ID，兼容 Sa-Token 登录态对象与字符串类型。
     */
    private Long getCurrentUserId() {
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            throw new BusinessException("用户登录态无效，请重新登录");
        }
    }
}
