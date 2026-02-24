package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-评价管理")
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "获取评价列表（分页）")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "回复评价")
    @PutMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "设为/取消精选")
    @PutMapping("/{id}/featured")
    public Result<?> toggleFeatured(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
