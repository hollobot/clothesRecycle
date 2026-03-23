package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.UserMapper;
import com.example.project.model.dto.user.ChangePasswordDto;
import com.example.project.model.dto.user.ResetPasswordBySmsDto;
import com.example.project.model.dto.user.UpdateMyProfileDto;
import com.example.project.model.po.User;
import com.example.project.model.vo.user.UserProfileVo;
import com.example.project.service.UserProfileService;
import com.example.project.utils.sms.SmsSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

/**
 * 用户端资料与密码服务实现。
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    /**
     * 密码重置短信验证码 Redis 键前缀。
     */
    private static final String RESET_PASSWORD_SMS_KEY_PREFIX = "sms:reset-password:";

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final SmsSender smsSender;

    public UserProfileServiceImpl(UserMapper userMapper,
                                  StringRedisTemplate stringRedisTemplate,
                                  SmsSender smsSender) {
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.smsSender = smsSender;
    }

    @Override
    public UserProfileVo getProfile(Long userId) {
        User user = requireUser(userId);
        return toProfileVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfileVo updateProfile(Long userId, UpdateMyProfileDto dto) {
        User user = requireUser(userId);

        if (!user.getStudentId().equals(dto.getStudentId())) {
            long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getStudentId, dto.getStudentId())
                    .ne(User::getId, userId));
            if (count > 0) {
                throw new BusinessException("学号已被占用");
            }
        }

        user.setName(dto.getName().trim());
        user.setStudentId(dto.getStudentId().trim());
        user.setCampusId(dto.getCampusId());
        user.setAvatarUrl(dto.getAvatarUrl() == null ? null : dto.getAvatarUrl().trim());
        userMapper.updateById(user);
        return toProfileVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDto dto) {
        User user = requireUser(userId);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        // 新旧密码相同会导致无效修改，直接拦截。
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);
    }

    @Override
    public void sendResetPasswordSms(Long userId) {
        User user = requireUser(userId);
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        String key = RESET_PASSWORD_SMS_KEY_PREFIX + user.getPhone();
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
        // 当前项目短信为模拟发送，验证码会打印到后端控制台日志。
        smsSender.sendCode(user.getPhone(), code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordBySms(Long userId, ResetPasswordBySmsDto dto) {
        User user = requireUser(userId);
        String key = RESET_PASSWORD_SMS_KEY_PREFIX + user.getPhone();
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        if (cacheCode == null || !cacheCode.equals(dto.getSmsCode())) {
            throw new BusinessException("验证码错误或已过期");
        }
        // 短信重置同样禁止与当前密码重复。
        if (dto.getNewPassword().equals(user.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        user.setPassword(dto.getNewPassword());
        userMapper.updateById(user);
        stringRedisTemplate.delete(key);
    }

    /**
     * 校验并读取用户。
     */
    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户状态异常");
        }
        return user;
    }

    /**
     * 用户实体转个人资料视图。
     */
    private UserProfileVo toProfileVo(User user) {
        UserProfileVo vo = new UserProfileVo();
        vo.setUserId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setName(user.getName());
        vo.setStudentId(user.getStudentId());
        vo.setCampusId(user.getCampusId());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }
}
