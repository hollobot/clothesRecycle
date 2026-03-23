package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.CampusMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.Campus;
import com.example.project.model.po.Order;
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
    private final OrderMapper orderMapper;

    public RankServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           UserMapper userMapper,
                           CampusMapper campusMapper,
                           OrderMapper orderMapper) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
        this.campusMapper = campusMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public Map<String, Object> getRankList(String type, Long campusId, int limit, Long currentUserId) {
        String key = buildRankKey(type, campusId);
        boolean useRedis = "campus".equals(type) || campusId != null;

        Set<ZSetOperations.TypedTuple<Object>> tuples = null;
        if (useRedis) {
            tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, Math.max(limit - 1, 0));
        }

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

        // Redis 暂无数据时，回落数据库实时统计，保证排行榜始终可展示。
        if (list.isEmpty()) {
            list = buildFallbackRankList(type, campusId, limit);
        }

        Map<String, Object> myRank = null;
        if (currentUserId != null) {
            if (useRedis) {
                Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(currentUserId));
                Double score = redisTemplate.opsForZSet().score(key, String.valueOf(currentUserId));
                if (rank != null) {
                    myRank = new HashMap<>();
                    myRank.put("rank", rank + 1);
                    myRank.put("score", score == null ? 0 : score.intValue());
                }
            }

            // Redis 中没有个人排名时，回落数据库计算，保证个人排名始终可见。
            if (myRank == null) {
                myRank = computeFallbackMyRank(type, campusId, currentUserId);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("campusId", campusId);
        result.put("updatedAt", java.time.LocalDateTime.now().toString());
        result.put("list", list);
        result.put("myRank", myRank);
        return result;
    }

    /**
     * 构建数据库回落榜单。
     */
    private List<Map<String, Object>> buildFallbackRankList(String type, Long campusId, int limit) {
        if ("points".equals(type)) {
            return buildPointFallbackRank(campusId, limit);
        }
        if ("donate".equals(type)) {
            return buildDonateFallbackRank(campusId, limit);
        }
        return buildCampusFallbackRank(limit);
    }

    /**
     * 积分榜回落：按用户积分余额倒序。
     */
    private List<Map<String, Object>> buildPointFallbackRank(Long campusId, int limit) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(campusId != null, User::getCampusId, campusId)
                .eq(User::getStatus, 1)
                .orderByDesc(User::getPointBalance)
                .last("LIMIT " + Math.max(limit, 1)));

        List<Map<String, Object>> list = new ArrayList<>();
        int rank = 1;
        for (User user : users) {
            Map<String, Object> row = new HashMap<>();
            row.put("rank", rank++);
            row.put("score", user.getPointBalance() == null ? 0 : user.getPointBalance());
            row.put("userId", user.getId());
            row.put("nickname", user.getName() == null || user.getName().isBlank() ? "匿名用户" : user.getName());
            row.put("avatar", user.getAvatarUrl() == null ? "" : user.getAvatarUrl());
            row.put("campusName", resolveCampusName(user.getCampusId()));
            list.add(row);
        }
        return list;
    }

    /**
     * 捐赠榜回落：按完成订单数倒序。
     */
    private List<Map<String, Object>> buildDonateFallbackRank(Long campusId, int limit) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(campusId != null, User::getCampusId, campusId)
                .eq(User::getStatus, 1));

        List<Map<String, Object>> rows = new ArrayList<>();
        for (User user : users) {
            Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                    .eq(Order::getSellerId, user.getId())
                    .eq(Order::getStatus, "DONE"));
            Map<String, Object> row = new HashMap<>();
            row.put("userId", user.getId());
            row.put("nickname", user.getName() == null || user.getName().isBlank() ? "匿名用户" : user.getName());
            row.put("avatar", user.getAvatarUrl() == null ? "" : user.getAvatarUrl());
            row.put("campusName", resolveCampusName(user.getCampusId()));
            row.put("score", count == null ? 0 : count.intValue());
            rows.add(row);
        }

        rows.sort((a, b) -> Integer.compare((int) b.get("score"), (int) a.get("score")));
        if (rows.size() > limit) {
            rows = new ArrayList<>(rows.subList(0, limit));
        }
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).put("rank", i + 1);
        }
        return rows;
    }

    /**
     * 校区榜回落：按完成订单数倒序。
     */
    private List<Map<String, Object>> buildCampusFallbackRank(int limit) {
        List<Campus> campuses = campusMapper.selectList(new LambdaQueryWrapper<Campus>().eq(Campus::getStatus, 1));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Campus campus : campuses) {
            Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                    .eq(Order::getCampusId, campus.getId())
                    .eq(Order::getStatus, "DONE"));
            Map<String, Object> row = new HashMap<>();
            row.put("campusId", campus.getId());
            row.put("campusName", campus.getName());
            row.put("score", count == null ? 0 : count.intValue());
            rows.add(row);
        }
        rows.sort((a, b) -> Integer.compare((int) b.get("score"), (int) a.get("score")));
        if (rows.size() > limit) {
            rows = new ArrayList<>(rows.subList(0, limit));
        }
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).put("rank", i + 1);
        }
        return rows;
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

    /**
     * 数据库回落计算个人排名。
     */
    private Map<String, Object> computeFallbackMyRank(String type, Long campusId, Long currentUserId) {
        List<Map<String, Object>> fullList = buildFallbackRankList(type, campusId, 10000);
        int score = 0;
        int rank = fullList.size() + 1;

        for (int i = 0; i < fullList.size(); i++) {
            Map<String, Object> row = fullList.get(i);
            Object userIdValue = row.get("userId");
            if (userIdValue != null && currentUserId.equals(Long.valueOf(String.valueOf(userIdValue)))) {
                rank = i + 1;
                score = (int) row.get("score");
                break;
            }
        }

        Map<String, Object> myRank = new HashMap<>();
        myRank.put("rank", rank);
        myRank.put("score", score);
        return myRank;
    }
}
