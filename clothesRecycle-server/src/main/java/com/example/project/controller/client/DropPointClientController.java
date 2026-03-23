package com.example.project.controller.client;

import com.example.project.common.result.Result;
import com.example.project.model.po.DropPoint;
import com.example.project.service.DropPointService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端回收点控制器。
 */
@RestController
@RequestMapping("/api/public/drop-points")
public class DropPointClientController {

    private final DropPointService dropPointService;

    public DropPointClientController(DropPointService dropPointService) {
        this.dropPointService = dropPointService;
    }

    /**
     * 查询启用回收点列表。
     */
    @GetMapping
    public Result<List<DropPoint>> list(@RequestParam(required = false) Long campusId) {
        return Result.ok(dropPointService.listPublicDropPoints(campusId));
    }
}
