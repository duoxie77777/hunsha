package com.hunsha.controller;

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

@Tag(name = "作品接口")
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "获取作品列表")
    @GetMapping
    public Result<PageResult<Portfolio>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        LambdaQueryWrapper<Portfolio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Portfolio::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Portfolio::getCategory, category);
        }
        wrapper.orderByAsc(Portfolio::getSortOrder).orderByDesc(Portfolio::getCreatedAt);
        Page<Portfolio> pageResult = portfolioService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "获取作品详情")
    @GetMapping("/{id}")
    public Result<Portfolio> detail(@PathVariable Long id) {
        Portfolio portfolio = portfolioService.getById(id);
        if (portfolio == null || portfolio.getStatus() != 1) {
            return Result.fail("作品不存在");
        }
        // 浏览量+1
        Portfolio update = new Portfolio();
        update.setId(id);
        update.setViewCount(portfolio.getViewCount() == null ? 1 : portfolio.getViewCount() + 1);
        portfolioService.updateById(update);
        portfolio.setViewCount(update.getViewCount());
        return Result.ok(portfolio);
    }
}
