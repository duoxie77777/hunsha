package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-订单管理")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "获取订单列表（分页）")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
