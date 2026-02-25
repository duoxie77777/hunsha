package com.hunsha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hunsha.dto.LoginDTO;
import com.hunsha.dto.RegisterDTO;
import com.hunsha.entity.User;
import com.hunsha.vo.LoginVO;
import com.hunsha.vo.UserVO;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户资料
     */
    void updateProfile(Long userId, User profile);
}
