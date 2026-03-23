package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.campus.SaveCampusDto;
import com.example.project.model.po.Campus;
import com.example.project.service.CampusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 超级管理员校区管理控制器。
 */
@RestController
@RequestMapping("/api/super/campuses")
public class CampusAdminController {

    private final CampusService campusService;

    public CampusAdminController(CampusService campusService) {
        this.campusService = campusService;
    }

    /**
     * 校区列表。
     */
    @GetMapping
    public Result<List<Campus>> list() {
        return Result.ok(campusService.listAllCampuses());
    }

    /**
     * 新增校区。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SaveCampusDto dto) {
        return Result.ok(campusService.createCampus(dto));
    }

    /**
     * 更新校区。
     */
    @PutMapping("/{campusId}")
    public Result<Void> update(@PathVariable Long campusId, @Valid @RequestBody SaveCampusDto dto) {
        campusService.updateCampus(campusId, dto);
        return Result.ok();
    }

    /**
     * 启用或禁用校区。
     */
    @PostMapping("/{campusId}/status")
    public Result<Void> changeStatus(@PathVariable Long campusId, @RequestParam boolean enabled) {
        campusService.changeCampusStatus(campusId, enabled);
        return Result.ok();
    }

    /**
     * 删除校区。
     */
    @DeleteMapping("/{campusId}")
    public Result<Void> delete(@PathVariable Long campusId) {
        campusService.deleteCampus(campusId);
        return Result.ok();
    }
}
