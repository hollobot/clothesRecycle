package com.example.project.service;

import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.model.po.DropPoint;

import java.util.List;

/**
 * 回收点业务接口。
 */
public interface DropPointService {

    List<DropPoint> listPublicDropPoints(Long campusId);

    /**
     * 管理端查询回收点列表。
     *
     * @param campusId          查询校区 ID（可选）
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    List<DropPoint> listAdminDropPoints(Long campusId, Long operatorCampusId);

    /**
     * 管理端新增回收点。
     *
     * @param dto               回收点保存参数
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    Long createDropPoint(SaveDropPointDto dto, Long operatorCampusId);

    /**
     * 管理端更新回收点。
     *
     * @param dropPointId       回收点 ID
     * @param dto               回收点保存参数
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    void updateDropPoint(Long dropPointId, SaveDropPointDto dto, Long operatorCampusId);

    /**
     * 管理端变更回收点状态。
     *
     * @param dropPointId       回收点 ID
     * @param enabled           是否启用
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    void changeDropPointStatus(Long dropPointId, boolean enabled, Long operatorCampusId);
}
