package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.mapper.SignRecordMapper;
import com.example.project.model.po.SignRecord;
import com.example.project.service.PointService;
import com.example.project.service.SignService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 签到业务实现。
 */
@Service
public class SignServiceImpl implements SignService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PointService pointService;
    private final SignRecordMapper signRecordMapper;

    public SignServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           PointService pointService,
                           SignRecordMapper signRecordMapper) {
        this.redisTemplate = redisTemplate;
        this.pointService = pointService;
        this.signRecordMapper = signRecordMapper;
    }

    @Override
    public Map<String, Object> signToday(Long userId) {
        LocalDate today = LocalDate.now();
        String key = buildSignKey(userId, today.getYear());
        int offset = today.getDayOfYear() - 1;

        Boolean signed = redisTemplate.opsForValue().getBit(key, offset);
        if (Boolean.TRUE.equals(signed)) {
            return getTodayStatus(userId);
        }

        redisTemplate.opsForValue().setBit(key, offset, true);
        redisTemplate.expire(key, 730, TimeUnit.DAYS);
        int continuousDays = calcContinuousDays(key, offset);
        int reward = calcSignReward(continuousDays);

        pointService.addPoint(userId, reward, "SIGN", Long.valueOf(today.getDayOfYear()), "每日签到奖励");

        SignRecord record = new SignRecord();
        record.setUserId(userId);
        record.setSignDate(today);
        record.setRewardPoint(reward);
        signRecordMapper.insert(record);

        Map<String, Object> result = new HashMap<>();
        result.put("signed", true);
        result.put("continuousDays", continuousDays);
        result.put("todayPoints", reward);
        return result;
    }

    @Override
    public Map<String, Object> getTodayStatus(Long userId) {
        LocalDate today = LocalDate.now();
        String key = buildSignKey(userId, today.getYear());
        int offset = today.getDayOfYear() - 1;
        Boolean signed = redisTemplate.opsForValue().getBit(key, offset);
        int continuousDays = calcContinuousDays(key, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("signed", Boolean.TRUE.equals(signed));
        result.put("continuousDays", continuousDays);
        result.put("todayPoints", Boolean.TRUE.equals(signed) ? calcSignReward(continuousDays) : 0);
        return result;
    }

    @Override
    public Map<String, Object> getYearData(Long userId, int year) {
        int days = Year.of(year).length();
        String key = buildSignKey(userId, year);
        List<Map<String, Object>> data = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.ofYearDay(year, i + 1);
            Boolean signedBit = redisTemplate.opsForValue().getBit(key, i);
            boolean signed = Boolean.TRUE.equals(signedBit);
            if (signed) {
                count++;
            }
            Map<String, Object> day = new HashMap<>();
            day.put("date", date.toString());
            day.put("signed", signed);
            data.add(day);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("totalDays", days);
        result.put("signedCount", count);
        int offset = (year == LocalDate.now().getYear()) ? LocalDate.now().getDayOfYear() - 1 : days - 1;
        result.put("continuousDays", calcContinuousDays(key, offset));
        result.put("data", data);
        return result;
    }

    private String buildSignKey(Long userId, int year) {
        return "sign:user:" + userId + ":" + year;
    }

    private int calcContinuousDays(String key, int offset) {
        int days = 0;
        for (int i = offset; i >= 0; i--) {
            Boolean bit = redisTemplate.opsForValue().getBit(key, i);
            if (!Boolean.TRUE.equals(bit)) {
                break;
            }
            days++;
        }
        return days;
    }

    private int calcSignReward(int continuousDays) {
        if (continuousDays >= 30) {
            return 30;
        }
        if (continuousDays >= 14) {
            return 15;
        }
        if (continuousDays >= 7) {
            return 10;
        }
        if (continuousDays >= 3) {
            return 5;
        }
        return 2;
    }
}
