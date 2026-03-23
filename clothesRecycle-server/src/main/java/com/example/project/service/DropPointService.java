package com.example.project.service;

import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.model.po.DropPoint;

import java.util.List;

/**
 * 回收点业务接口。
 */
public interface DropPointService {

    List<DropPoint> listPublicDropPoints(Long campusId);

    List<DropPoint> listAdminDropPoints(Long campusId);

    Long createDropPoint(SaveDropPointDto dto);

    void updateDropPoint(Long dropPointId, SaveDropPointDto dto);

    void changeDropPointStatus(Long dropPointId, boolean enabled);
}
