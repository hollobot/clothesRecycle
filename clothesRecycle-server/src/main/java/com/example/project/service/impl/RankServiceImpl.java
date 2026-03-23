package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.CampusMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.Campus;
import com.example.project.model.po.User;
import com.example.project.service.RankService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 排行榜业务实现。
 */
@Service
public class RankServiceImpl implements RankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;
    private final CampusMapper campusMapper;

    public RankServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           UserMapper userMapper,
                           CampusMapper campusMapper) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
        this.campusMapper = campusMapper;
    }

    @Override
    public Map<String, Object> getRankList(String type, Long campusId, int limit, Long currentUserId) {
        if (!"campus".equals(type) && campusId == null) {
            throw new BusinessException("campusId不能为空");
        }
        String key = buildRankKey(type, campusId);
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, Math.max(limit - 1, 0));

        List<Map<String, Object>> list = new ArrayList<>();
        if (tuples != null) {
            int rank = 1;
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                Map<String, Object> row = new HashMap<>();
                row.put("rank", rank++);
                row.put("score", tuple.getScore() == null ? 0 : tuple.getScore().intValue());
                if ("campus".equals(type)) {
                    Long rankedCampusId = Long.valueOf(String.valueOf(tuple.getValue()));
                    Campus campus = campusMapper.selectById(rankedCampusId);
                    row.put("campusId", rankedCampusId);
                    row.put("campusName", campus == null ? "未知校区" : campus.getName());
                } else {
                    Long userId = Long.valueOf(String.valueOf(tuple.getValue()));
                    User user = userMapper.selectById(userId);
                    row.put("userId", userId);
                    row.put("nickname", user == null ? "未知用户" : user.getName());
                    row.put("avatar", user == null ? "" : user.getAvatarUrl());
                    row.put("campusName", user == null ? "" : resolveCampusName(user.getCampusId()));
                }
                list.add(row);
            }
        }

        Map<String, Object> myRank = null;
        if (currentUserId != null) {
            Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(currentUserId));
            Double score = redisTemplate.opsForZSet().score(key, String.valueOf(currentUserId));
            myRank = new HashMap<>();
            myRank.put("rank", rank == null ? null : rank + 1);
            myRank.put("score", score == null ? 0 : score.intValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("campusId", campusId);
        result.put("updatedAt", java.time.LocalDateTime.now().toString());
        result.put("list", list);
        result.put("myRank", myRank);
        return result;
    }

    private String resolveCampusName(Long campusId) {
        if (campusId == null) {
            return "";
        }
        Campus campus = campusMapper.selectById(campusId);
        return campus == null ? "" : campus.getName();
    }

    private String buildRankKey(String type, Long campusId) {
        if ("campus".equals(type)) {
            return "rank:campus:orders";
        }
        return "rank:" + type + ":" + campusId;
    }
}
