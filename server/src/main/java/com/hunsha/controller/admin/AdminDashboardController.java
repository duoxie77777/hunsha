package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.OrderService;
import com.hunsha.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-数据看板")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderService orderService;
    private final UserService userService;

    @Operation(summary = "获取看板统计数据")
    @GetMapping("/stats")
    public Result<?> stats() {
        // TODO
        return Result.ok();
    }
}
