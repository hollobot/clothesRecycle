package com.example.project.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.config.SaTokenConfig;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.UserMapper;
import com.example.project.model.dto.auth.LoginDto;
import com.example.project.model.dto.auth.RegisterDto;
import com.example.project.model.dto.auth.SendSmsDto;
import com.example.project.model.po.User;
import com.example.project.model.vo.auth.LoginVo;
import com.example.project.service.AuthService;
import com.example.project.service.PointService;
import com.example.project.utils.sms.SmsSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

/**
 * 认证业务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String REGISTER_SMS_KEY_PREFIX = "sms:register:";

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final SmsSender smsSender;
    private final PointService pointService;

    public AuthServiceImpl(UserMapper userMapper,
                           StringRedisTemplate stringRedisTemplate,
                           SmsSender smsSender,
                           PointService pointService) {
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.smsSender = smsSender;
        this.pointService = pointService;
    }

    @Override
    public void sendRegisterSmsCode(SendSmsDto dto) {
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        String key = REGISTER_SMS_KEY_PREFIX + dto.getPhone();
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(1));
        smsSender.sendCode(dto.getPhone(), code);
    }

    @Override
    public LoginVo register(RegisterDto dto) {
        String key = REGISTER_SMS_KEY_PREFIX + dto.getPhone();
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        if (cacheCode == null || !cacheCode.equals(dto.getSmsCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(phoneQuery) > 0) {
            throw new BusinessException("手机号已注册");
        }
        LambdaQueryWrapper<User> sidQuery = new LambdaQueryWrapper<User>().eq(User::getStudentId, dto.getStudentId());
        if (userMapper.selectCount(sidQuery) > 0) {
            throw new BusinessException("学号已注册");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setStudentId(dto.getStudentId());
        user.setName(dto.getName());
        user.setCampusId(dto.getCampusId());
        user.setStatus(1);
        user.setPointBalance(0);
        user.setFrozenPoint(0);
        userMapper.insert(user);

        pointService.addPoint(user.getId(), 50, "REGISTER", user.getId(), "注册奖励");
        stringRedisTemplate.delete(key);
        return doLogin(user);
    }

    @Override
    public LoginVo login(LoginDto dto) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone())
                .eq(User::getPassword, dto.getPassword())
                .eq(User::getStatus, 1);
        User user = userMapper.selectOne(query);
        if (user == null) {
            throw new BusinessException("手机号或密码错误");
        }
        return doLogin(user);
    }

    /**
     * 执行统一登录并构建返回结果。
     */
    private LoginVo doLogin(User user) {
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        SaTokenConfig.logLoginEvent(user.getPhone(), user.getStudentId(), token);

        LoginVo vo = new LoginVo();
        vo.setUserId(user.getId());
        vo.setToken(token);
        vo.setRole("USER");
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setStudentId(user.getStudentId());
        vo.setCampusId(user.getCampusId());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setPointBalance(user.getPointBalance());
        return vo;
    }
}
