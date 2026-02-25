package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.entity.Order;
import com.hunsha.entity.User;
import com.hunsha.service.OrderService;
import com.hunsha.service.UserService;
import com.hunsha.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "后台-客户管理")
@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final UserService userService;
    private final OrderService orderService;

    @Operation(summary = "获取客户列表（分页）")
    @GetMapping
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, 0); // 只查普通用户
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        wrapper.select(User::getId, User::getUsername, User::getNickname, User::getPhone,
                User::getEmail, User::getAvatar, User::getStatus, User::getCreatedAt);
        Page<User> pageResult = userService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("客户不存在");
        }
        Map<String, Object> data = new HashMap<>();

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setAvatar(user.getAvatar());
        data.put("user", userVO);

        // 该客户的订单列表
        List<Order> orders = orderService.list(
                new LambdaQueryWrapper<Order>().eq(Order::getUserId, id).orderByDesc(Order::getCreatedAt));
        data.put("orders", orders);

        return Result.ok(data);
    }

    @Operation(summary = "启用/禁用客户")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = new User();
        user.setId(id);
        user.setStatus(body.get("status"));
        userService.updateById(user);
        return Result.ok();
    }
}
