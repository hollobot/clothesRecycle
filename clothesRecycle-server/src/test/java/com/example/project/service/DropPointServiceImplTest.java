package com.example.project.service;

import com.example.project.exception.BusinessException;
import com.example.project.mapper.DropPointMapper;
import com.example.project.model.dto.drop.SaveDropPointDto;
import com.example.project.service.impl.DropPointServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 回收点管理校区权限测试。
 */
@ExtendWith(MockitoExtension.class)
class DropPointServiceImplTest {

    /**
     * 回收点数据访问。
     */
    @Mock
    private DropPointMapper dropPointMapper;

    /**
     * 待测回收点业务实现。
     */
    @InjectMocks
    private DropPointServiceImpl dropPointService;

    @Test
    void should_reject_list_drop_points_when_operator_requests_other_campus() {
        assertThatThrownBy(() -> dropPointService.listAdminDropPoints(2L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("无权限查询其他校区回收点");
    }

    @Test
    void should_reject_create_drop_point_when_operator_submits_other_campus() {
        SaveDropPointDto dto = new SaveDropPointDto();
        dto.setCampusId(2L);
        dto.setName("测试回收点");
        dto.setLocationDesc("A 区一楼");
        dto.setOpenTime("09:00-18:00");

        assertThatThrownBy(() -> dropPointService.createDropPoint(dto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("无权限新增其他校区回收点");
    }
}
