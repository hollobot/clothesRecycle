package com.example.project.service;

import com.example.project.model.dto.campus.SaveCampusDto;
import com.example.project.model.po.Campus;

import java.util.List;

/**
 * 校区业务接口。
 */
public interface CampusService {

    List<Campus> listEnabledCampuses();

    List<Campus> listAllCampuses();

    Long createCampus(SaveCampusDto dto);

    void updateCampus(Long campusId, SaveCampusDto dto);

    void changeCampusStatus(Long campusId, boolean enabled);

    void deleteCampus(Long campusId);
}
