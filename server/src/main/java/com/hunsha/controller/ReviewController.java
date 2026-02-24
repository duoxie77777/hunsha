package com.hunsha.controller;

import com.hunsha.common.Result;
import com.hunsha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评价接口")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "获取评价列表")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<?> create() {
        // TODO
        return Result.ok();
    }
}
