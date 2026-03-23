package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.CampusMapper;
import com.example.project.model.dto.campus.SaveCampusDto;
import com.example.project.model.po.Campus;
import com.example.project.service.CampusService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 校区业务实现。
 */
@Service
public class CampusServiceImpl implements CampusService {

    private final CampusMapper campusMapper;

    public CampusServiceImpl(CampusMapper campusMapper) {
        this.campusMapper = campusMapper;
    }

    @Override
    public List<Campus> listEnabledCampuses() {
        LambdaQueryWrapper<Campus> query = new LambdaQueryWrapper<Campus>()
                .eq(Campus::getStatus, 1)
                .orderByAsc(Campus::getId);
        return campusMapper.selectList(query);
    }

    @Override
    public List<Campus> listAllCampuses() {
        LambdaQueryWrapper<Campus> query = new LambdaQueryWrapper<Campus>()
                .orderByDesc(Campus::getCreateTime);
        return campusMapper.selectList(query);
    }

    @Override
    public Long createCampus(SaveCampusDto dto) {
        Campus campus = new Campus();
        BeanUtils.copyProperties(dto, campus);
        campus.setRemark(dto.getRemark() == null ? "" : dto.getRemark());
        campus.setStatus(1);
        campusMapper.insert(campus);
        return campus.getId();
    }

    @Override
    public void updateCampus(Long campusId, SaveCampusDto dto) {
        Campus campus = campusMapper.selectById(campusId);
        if (campus == null) {
            throw new BusinessException("校区不存在");
        }
        BeanUtils.copyProperties(dto, campus);
        campus.setRemark(dto.getRemark() == null ? "" : dto.getRemark());
        campusMapper.updateById(campus);
    }

    @Override
    public void changeCampusStatus(Long campusId, boolean enabled) {
        Campus campus = campusMapper.selectById(campusId);
        if (campus == null) {
            throw new BusinessException("校区不存在");
        }
        campus.setStatus(enabled ? 1 : 0);
        campusMapper.updateById(campus);
    }

    @Override
    public void deleteCampus(Long campusId) {
        Campus campus = campusMapper.selectById(campusId);
        if (campus == null) {
            throw new BusinessException("校区不存在");
        }
        campusMapper.deleteById(campusId);
    }
}
