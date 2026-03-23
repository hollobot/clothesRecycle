package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.model.po.DropPoint;
import com.example.project.service.DropPointService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端回收点控制器。
 */
@RestController
@RequestMapping("/api/admin/drop-points")
public class DropPointAdminController {

    private final DropPointService dropPointService;

    public DropPointAdminController(DropPointService dropPointService) {
        this.dropPointService = dropPointService;
    }

    /**
     * 回收点列表。
     */
    @GetMapping
    public Result<List<DropPoint>> list(@RequestParam(required = false) Long campusId) {
        return Result.ok(dropPointService.listAdminDropPoints(campusId));
    }

    /**
     * 新增回收点。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SaveDropPointDto dto) {
        return Result.ok(dropPointService.createDropPoint(dto));
    }

    /**
     * 编辑回收点。
     */
    @PutMapping("/{dropPointId}")
    public Result<Void> update(@PathVariable Long dropPointId, @Valid @RequestBody SaveDropPointDto dto) {
        dropPointService.updateDropPoint(dropPointId, dto);
        return Result.ok();
    }

    /**
     * 启用或禁用回收点。
     */
    @PostMapping("/{dropPointId}/status")
    public Result<Void> changeStatus(@PathVariable Long dropPointId, @RequestParam boolean enabled) {
        dropPointService.changeDropPointStatus(dropPointId, enabled);
        return Result.ok();
    }
}
