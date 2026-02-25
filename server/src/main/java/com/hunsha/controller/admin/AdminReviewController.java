package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.entity.Review;
import com.hunsha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "后台-评价管理")
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "获取评价列表（分页）")
    @GetMapping
    public Result<PageResult<Review>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer isFeatured,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        if (isFeatured != null) {
            wrapper.eq(Review::getIsFeatured, isFeatured);
        }
        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }
        wrapper.orderByDesc(Review::getCreatedAt);
        Page<Review> pageResult = reviewService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "回复评价")
    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.fail("评价不存在");
        }
        review.setAdminReply(body.get("reply"));
        review.setReplyAt(LocalDateTime.now());
        reviewService.updateById(review);
        return Result.ok();
    }

    @Operation(summary = "设为/取消精选")
    @PutMapping("/{id}/featured")
    public Result<Void> toggleFeatured(@PathVariable Long id) {
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.fail("评价不存在");
        }
        review.setIsFeatured(review.getIsFeatured() == 1 ? 0 : 1);
        reviewService.updateById(review);
        return Result.ok();
    }

    @Operation(summary = "显示/隐藏评价")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Review review = new Review();
        review.setId(id);
        review.setStatus(body.get("status"));
        reviewService.updateById(review);
        return Result.ok();
    }
}
