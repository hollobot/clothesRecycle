package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.model.po.Admin;
import com.example.project.model.po.DropPoint;
import com.example.project.service.DropPointService;
import com.example.project.service.support.AdminSessionService;
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

    /**
     * 回收点业务。
     */
    private final DropPointService dropPointService;
    /**
     * 管理端会话与权限范围服务。
     */
    private final AdminSessionService adminSessionService;

    public DropPointAdminController(DropPointService dropPointService,
                                    AdminSessionService adminSessionService) {
        this.dropPointService = dropPointService;
        this.adminSessionService = adminSessionService;
    }

    /**
     * 回收点列表。
     */
    @GetMapping
    public Result<List<DropPoint>> list(@RequestParam(required = false) Long campusId) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, campusId);
        return Result.ok(dropPointService.listAdminDropPoints(scopedCampusId, scopedCampusId));
    }

    /**
     * 新增回收点。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SaveDropPointDto dto) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, dto.getCampusId());
        return Result.ok(dropPointService.createDropPoint(dto, scopedCampusId));
    }

    /**
     * 编辑回收点。
     */
    @PutMapping("/{dropPointId}")
    public Result<Void> update(@PathVariable Long dropPointId, @Valid @RequestBody SaveDropPointDto dto) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, dto.getCampusId());
        dropPointService.updateDropPoint(dropPointId, dto, scopedCampusId);
        return Result.ok();
    }

    /**
     * 启用或禁用回收点。
     */
    @PostMapping("/{dropPointId}/status")
    public Result<Void> changeStatus(@PathVariable Long dropPointId, @RequestParam boolean enabled) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        dropPointService.changeDropPointStatus(dropPointId, enabled, scopedCampusId);
        return Result.ok();
    }
}
