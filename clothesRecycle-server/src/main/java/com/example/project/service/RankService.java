package com.example.project.service;

import java.util.Map;

/**
 * 排行榜业务接口。
 */
public interface RankService {

    Map<String, Object> getRankList(String type, Long campusId, int limit, Long currentUserId);
}
