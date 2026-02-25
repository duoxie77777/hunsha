package com.hunsha.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.BusinessException;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.dto.ReviewDTO;
import com.hunsha.entity.Order;
import com.hunsha.entity.Review;
import com.hunsha.service.OrderService;
import com.hunsha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评价接口")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final OrderService orderService;

    @Operation(summary = "获取评价列表")
    @GetMapping
    public Result<PageResult<Review>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer featured) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStatus, 1);
        if (featured != null && featured == 1) {
            wrapper.eq(Review::getIsFeatured, 1);
        }
        wrapper.orderByDesc(Review::getCreatedAt);
        Page<Review> pageResult = reviewService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<Void> create(@RequestBody ReviewDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // 检查订单是否存在且属于当前用户
        Order order = orderService.getById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!"已完成".equals(order.getStatus())) {
            throw new BusinessException("只有已完成的订单才能评价");
        }

        // 检查是否已评价
        long count = reviewService.count(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderId, dto.getOrderId())
                .eq(Review::getUserId, userId));
        if (count > 0) {
            throw new BusinessException("该订单已评价");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setIsFeatured(0);
        review.setStatus(1);
        reviewService.save(review);
        return Result.ok();
    }
}
