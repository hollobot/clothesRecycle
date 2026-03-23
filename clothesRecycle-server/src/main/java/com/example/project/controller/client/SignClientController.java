package com.example.project.controller.client;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.result.Result;
import com.example.project.service.SignService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 用户端签到控制器。
 */
@RestController
@RequestMapping("/api/user/sign")
public class SignClientController {

    private final SignService signService;

    public SignClientController(SignService signService) {
        this.signService = signService;
    }

    /**
     * 今日签到。
     */
    @PostMapping("/today")
    public Result<Map<String, Object>> signToday() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        return Result.ok(signService.signToday(userId));
    }

    /**
     * 查询今日签到状态。
     */
    @GetMapping("/today")
    public Result<Map<String, Object>> today() {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        return Result.ok(signService.getTodayStatus(userId));
    }

    /**
     * 查询年度签到数据。
     */
    @GetMapping("/year")
    public Result<Map<String, Object>> year(@RequestParam(required = false) Integer year) {
        Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        int queryYear = year == null ? LocalDate.now().getYear() : year;
        return Result.ok(signService.getYearData(userId, queryYear));
    }
}
