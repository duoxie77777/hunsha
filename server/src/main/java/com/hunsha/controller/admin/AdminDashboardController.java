package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hunsha.common.Result;
import com.hunsha.entity.Order;
import com.hunsha.entity.User;
import com.hunsha.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "后台-数据看板")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderService orderService;
    private final UserService userService;
    private final PackageService packageService;
    private final PortfolioService portfolioService;
    private final ReviewService reviewService;
    private final ScheduleService scheduleService;

    @Operation(summary = "获取看板统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        // 用户总数
        long userCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, 0));
        data.put("userCount", userCount);

        // 订单总数
        long orderCount = orderService.count();
        data.put("orderCount", orderCount);

        // 各状态订单数
        Map<String, Long> orderStatusMap = new HashMap<>();
        List<Order> allOrders = orderService.list();
        for (Order order : allOrders) {
            orderStatusMap.merge(order.getStatus(), 1L, Long::sum);
        }
        data.put("orderStatusMap", orderStatusMap);

        // 总营收（已完成订单金额之和）
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> "已完成".equals(o.getStatus()))
                .map(Order::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalRevenue", totalRevenue);

        // 套餐数
        long packageCount = packageService.count();
        data.put("packageCount", packageCount);

        // 作品数
        long portfolioCount = portfolioService.count();
        data.put("portfolioCount", portfolioCount);

        // 评价数
        long reviewCount = reviewService.count();
        data.put("reviewCount", reviewCount);

        // 档期数
        long scheduleCount = scheduleService.count();
        data.put("scheduleCount", scheduleCount);

        return Result.ok(data);
    }
}
