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
    public List<DropPoint> listAdminDropPoints(Long campusId) {
        LambdaQueryWrapper<DropPoint> query = new LambdaQueryWrapper<DropPoint>()
                .eq(campusId != null, DropPoint::getCampusId, campusId)
                .orderByDesc(DropPoint::getCreateTime);
        return dropPointMapper.selectList(query);
    }

    @Override
    public Long createDropPoint(SaveDropPointDto dto) {
        DropPoint dropPoint = new DropPoint();
        BeanUtils.copyProperties(dto, dropPoint);
        dropPoint.setManagerNote(dto.getManagerNote() == null ? "" : dto.getManagerNote());
        dropPoint.setStatus(1);
        dropPoint.setStockCount(0);
        dropPointMapper.insert(dropPoint);
        return dropPoint.getId();
    }

    @Override
    public void updateDropPoint(Long dropPointId, SaveDropPointDto dto) {
        DropPoint dropPoint = dropPointMapper.selectById(dropPointId);
        if (dropPoint == null) {
            throw new BusinessException("回收点不存在");
        }
        BeanUtils.copyProperties(dto, dropPoint);
        dropPoint.setManagerNote(dto.getManagerNote() == null ? "" : dto.getManagerNote());
        dropPointMapper.updateById(dropPoint);
    }

    @Override
    public void changeDropPointStatus(Long dropPointId, boolean enabled) {
        DropPoint dropPoint = dropPointMapper.selectById(dropPointId);
        if (dropPoint == null) {
            throw new BusinessException("回收点不存在");
        }
        dropPoint.setStatus(enabled ? 1 : 0);
        dropPointMapper.updateById(dropPoint);
    }
}
