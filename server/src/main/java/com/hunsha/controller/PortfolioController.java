package com.hunsha.controller;

import com.hunsha.common.Result;
import com.hunsha.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "作品接口")
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "获取作品列表")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "获取作品详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
