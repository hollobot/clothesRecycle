package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.constant.SystemConstant;
import com.example.project.common.result.Result;
import com.example.project.mapper.PointRecordMapper;
import com.example.project.model.po.PointRecord;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端积分控制器。
 */
@RestController
@RequestMapping("/api/user/points")
public class PointClientController {

    private final UserMapper userMapper;
    private final PointRecordMapper pointRecordMapper;

    public PointClientController(UserMapper userMapper, PointRecordMapper pointRecordMapper) {
        this.userMapper = userMapper;
        this.pointRecordMapper = pointRecordMapper;
    }

    /**
     * 查询当前积分账户信息。
     */
    @GetMapping("/account")
    public Result<Map<String, Object>> account() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        User user = userMapper.selectById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("balance", user == null ? 0 : user.getPointBalance());
        data.put("frozen", user == null ? 0 : user.getFrozenPoint());
        return Result.ok(data);
    }

    /**
     * 积分明细分页。
     */
    @GetMapping("/records")
    public Result<Map<String, Object>> records(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize
    ) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        long queryPageNum = pageNum == null || pageNum <= 0 ? SystemConstant.DEFAULT_PAGE_NUM : pageNum;
        long queryPageSize = pageSize == null || pageSize <= 0 ? SystemConstant.DEFAULT_PAGE_SIZE : pageSize;

        Page<PointRecord> page = new Page<>(queryPageNum, queryPageSize);
        LambdaQueryWrapper<PointRecord> query = new LambdaQueryWrapper<PointRecord>()
                .eq(PointRecord::getUserId, userId)
                .orderByDesc(PointRecord::getCreateTime);
        pointRecordMapper.selectPage(page, query);

        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.ok(data);
    }
}
