package com.example.project.service;

import com.example.project.exception.BusinessException;
import com.example.project.mapper.UserMapper;
import com.example.project.model.po.User;
import com.example.project.service.impl.UserManageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * 用户管理校区权限测试。
 */
@ExtendWith(MockitoExtension.class)
class UserManageServiceImplTest {

    /**
     * 用户数据访问。
     */
    @Mock
    private UserMapper userMapper;

    /**
     * 待测用户管理业务实现。
     */
    @InjectMocks
    private UserManageServiceImpl userManageService;

    @Test
    void should_reject_list_users_when_operator_requests_other_campus() {
        assertThatThrownBy(() -> userManageService.listUsers(2L, null, null, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("无权限查询其他校区用户");
    }

    @Test
    void should_reject_detail_when_operator_views_other_campus_user() {
        User user = new User();
        user.setId(88L);
        user.setCampusId(2L);
        when(userMapper.selectById(88L)).thenReturn(user);

        assertThatThrownBy(() -> userManageService.getUserDetail(88L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("无权限查看其他校区用户");
    }
}
