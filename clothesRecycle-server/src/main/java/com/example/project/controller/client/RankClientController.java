package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.User;
import com.example.project.service.RankService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 排行榜控制器。
 */
@RestController
public class RankClientController {

    private final RankService rankService;
    private final UserMapper userMapper;

    public RankClientController(RankService rankService, UserMapper userMapper) {
        this.rankService = rankService;
        this.userMapper = userMapper;
    }

    /**
     * 公共查询榜单。
     */
    @GetMapping("/api/public/rank/{type}")
    public Result<Map<String, Object>> list(@PathVariable String type,
                                            @RequestParam(required = false) Long campusId,
                                            @RequestParam(defaultValue = "50") Integer limit) {
        Long currentUserId = null;
        if (StpUtil.isLogin()) {
            currentUserId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        }
        return Result.ok(rankService.getRankList(type, campusId, limit, currentUserId));
    }

    /**
     * 查询我的排名。
     */
    @GetMapping("/api/user/rank/mine")
    public Result<Map<String, Object>> mine(@RequestParam Long campusId) {
        Long currentUserId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));

        Map<String, Object> pointsRank = rankService.getRankList("points", campusId, 50, currentUserId);
        Map<String, Object> donateRank = rankService.getRankList("donate", campusId, 50, currentUserId);

        User user = userMapper.selectById(currentUserId);
        Long userCampusId = user == null ? campusId : user.getCampusId();
        Map<String, Object> campusRank = rankService.getRankList("campus", userCampusId, 50, null);

        Map<String, Object> result = new HashMap<>();
        result.put("points", pointsRank.get("myRank"));
        result.put("donate", donateRank.get("myRank"));
        result.put("campus", campusRank.get("list"));
        return Result.ok(result);
    }
}
