package com.hunsha.controller;

import com.hunsha.common.Result;
import com.hunsha.dto.LoginDTO;
import com.hunsha.dto.RegisterDTO;
import com.hunsha.service.UserService;
import com.hunsha.vo.LoginVO;
import com.hunsha.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return Result.ok(null, "注册成功");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.ok(vo, "登录成功");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.getUserInfo(userId);
        return Result.ok(vo);
    }
}
