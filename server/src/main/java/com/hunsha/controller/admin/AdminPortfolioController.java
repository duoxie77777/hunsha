package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-作品管理")
@RestController
@RequestMapping("/api/admin/portfolios")
@RequiredArgsConstructor
public class AdminPortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "获取作品列表（分页）")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "新增作品")
    @PostMapping
    public Result<?> create() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "更新作品")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "删除作品")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
