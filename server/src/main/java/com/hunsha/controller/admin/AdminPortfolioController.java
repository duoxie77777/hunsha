package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.entity.Portfolio;
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
    public Result<PageResult<Portfolio>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Portfolio> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Portfolio::getTitle, title);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Portfolio::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Portfolio::getStatus, status);
        }
        wrapper.orderByAsc(Portfolio::getSortOrder).orderByDesc(Portfolio::getCreatedAt);
        Page<Portfolio> pageResult = portfolioService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "新增作品")
    @PostMapping
    public Result<Void> create(@RequestBody Portfolio portfolio) {
        if (portfolio.getViewCount() == null) {
            portfolio.setViewCount(0);
        }
        portfolioService.save(portfolio);
        return Result.ok();
    }

    @Operation(summary = "更新作品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Portfolio portfolio) {
        portfolio.setId(id);
        portfolioService.updateById(portfolio);
        return Result.ok();
    }

    @Operation(summary = "删除作品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        portfolioService.removeById(id);
        return Result.ok();
    }
}
