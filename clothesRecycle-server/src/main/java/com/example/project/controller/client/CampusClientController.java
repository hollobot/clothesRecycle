package com.example.project.controller.client;

import com.example.project.common.result.Result;
import com.example.project.model.po.Campus;
import com.example.project.service.CampusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端校区控制器。
 */
@RestController
@RequestMapping("/api/public/campus")
public class CampusClientController {

    private final CampusService campusService;

    public CampusClientController(CampusService campusService) {
        this.campusService = campusService;
    }

    /**
     * 查询启用校区列表。
     */
    @GetMapping("/list")
    public Result<List<Campus>> list() {
        return Result.ok(campusService.listEnabledCampuses());
    }
}
