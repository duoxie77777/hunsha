package com.hunsha.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.common.BusinessException;
import com.hunsha.dto.LoginDTO;
import com.hunsha.dto.RegisterDTO;
import com.hunsha.entity.User;
import com.hunsha.mapper.UserMapper;
import com.hunsha.service.UserService;
import com.hunsha.utils.JwtUtils;
import com.hunsha.vo.LoginVO;
import com.hunsha.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long usernameCount = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (usernameCount > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已注册
        long phoneCount = count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone()));
        if (phoneCount > 0) {
            throw new BusinessException("手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setRole(0);
        user.setStatus(1);

        save(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());
        String token = jwtUtils.generateToken(user.getId(), claims);

        // 构建返回
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setToken(token);

        return vo;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setCreatedAt(user.getCreatedAt());

        return vo;
    }

    @Override
    public void updateProfile(Long userId, User profile) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (profile.getNickname() != null) {
            user.setNickname(profile.getNickname());
        }
        if (profile.getAvatar() != null) {
            user.setAvatar(profile.getAvatar());
        }
        if (profile.getEmail() != null) {
            user.setEmail(profile.getEmail());
        }
        updateById(user);
    }
}
