package com.example.project.service;

import java.util.Map;

/**
 * 签到业务接口。
 */
public interface SignService {

    Map<String, Object> signToday(Long userId);

    Map<String, Object> getTodayStatus(Long userId);

    Map<String, Object> getYearData(Long userId, int year);
}
