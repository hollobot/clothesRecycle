package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.DropPointMapper;
import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.model.po.DropPoint;
import com.example.project.service.DropPointService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 回收点业务实现。
 */
@Service
public class DropPointServiceImpl implements DropPointService {

    private final DropPointMapper dropPointMapper;

    public DropPointServiceImpl(DropPointMapper dropPointMapper) {
        this.dropPointMapper = dropPointMapper;
    }

    @Override
    public List<DropPoint> listPublicDropPoints(Long campusId) {
        LambdaQueryWrapper<DropPoint> query = new LambdaQueryWrapper<DropPoint>()
                .eq(campusId != null, DropPoint::getCampusId, campusId)
                .eq(DropPoint::getStatus, 1)
                .orderByAsc(DropPoint::getId);
        return dropPointMapper.selectList(query);
    }

    @Override
    public List<DropPoint> listAdminDropPoints(Long campusId, Long operatorCampusId) {
        validateCampusAccess(operatorCampusId, campusId, "查询");
        LambdaQueryWrapper<DropPoint> query = new LambdaQueryWrapper<DropPoint>()
                .eq(campusId != null, DropPoint::getCampusId, campusId)
                .orderByDesc(DropPoint::getCreateTime);
        return dropPointMapper.selectList(query);
    }

    @Override
    public Long createDropPoint(SaveDropPointDto dto, Long operatorCampusId) {
        validateCampusAccess(operatorCampusId, dto.getCampusId(), "新增");
        DropPoint dropPoint = new DropPoint();
        BeanUtils.copyProperties(dto, dropPoint);
        dropPoint.setManagerNote(dto.getManagerNote() == null ? "" : dto.getManagerNote());
        dropPoint.setStatus(1);
        dropPoint.setStockCount(0);
        dropPointMapper.insert(dropPoint);
        return dropPoint.getId();
    }

    @Override
    public void updateDropPoint(Long dropPointId, SaveDropPointDto dto, Long operatorCampusId) {
        validateCampusAccess(operatorCampusId, dto.getCampusId(), "修改");
        DropPoint dropPoint = dropPointMapper.selectById(dropPointId);
        if (dropPoint == null) {
            throw new BusinessException("回收点不存在");
        }
        validateCampusAccess(operatorCampusId, dropPoint.getCampusId(), "修改");
        BeanUtils.copyProperties(dto, dropPoint);
        dropPoint.setManagerNote(dto.getManagerNote() == null ? "" : dto.getManagerNote());
        dropPointMapper.updateById(dropPoint);
    }

    @Override
    public void changeDropPointStatus(Long dropPointId, boolean enabled, Long operatorCampusId) {
        DropPoint dropPoint = dropPointMapper.selectById(dropPointId);
        if (dropPoint == null) {
            throw new BusinessException("回收点不存在");
        }
        validateCampusAccess(operatorCampusId, dropPoint.getCampusId(), "操作");
        dropPoint.setStatus(enabled ? 1 : 0);
        dropPointMapper.updateById(dropPoint);
    }

    /**
     * 校验校区管理员是否可操作目标校区数据。
     *
     * @param operatorCampusId 操作管理员可管理校区，超级管理员为 null
     * @param targetCampusId   目标数据所属校区
     * @param action           业务动作描述
     */
    private void validateCampusAccess(Long operatorCampusId, Long targetCampusId, String action) {
        if (operatorCampusId != null && !operatorCampusId.equals(targetCampusId)) {
            throw new BusinessException("无权限" + action + "其他校区回收点");
        }
    }
}
