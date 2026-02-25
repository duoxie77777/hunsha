package com.hunsha.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hunsha.common.BusinessException;
import com.hunsha.common.Result;
import com.hunsha.dto.OrderDTO;
import com.hunsha.entity.Order;
import com.hunsha.entity.Package;
import com.hunsha.service.OrderService;
import com.hunsha.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PackageService packageService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> create(@RequestBody OrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Package pkg = packageService.getById(dto.getPackageId());
        if (pkg == null || pkg.getStatus() != 1) {
            throw new BusinessException("套餐不存在或已下架");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageId(pkg.getId());
        order.setPackageName(pkg.getName());
        order.setAmount(pkg.getPrice());
        order.setStatus("待确认");
        order.setShootDate(dto.getShootDate());
        order.setShootTime(dto.getShootTime());
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setRemark(dto.getRemark());
        orderService.save(order);
        return Result.ok(order);
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping("/mine")
    public Result<List<Order>> myOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId).orderByDesc(Order::getCreatedAt);
        List<Order> orders = orderService.list(wrapper);
        return Result.ok(orders);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!"待确认".equals(order.getStatus()) && !"已确认".equals(order.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        order.setStatus("已取消");
        orderService.updateById(order);
        return Result.ok();
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.fail("订单不存在");
        }
        return Result.ok(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "HS" + timestamp + random;
    }
}
