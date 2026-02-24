package com.hunsha.controller;

import com.hunsha.common.Result;
import com.hunsha.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<?> create() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping("/mine")
    public Result<?> myOrders() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
