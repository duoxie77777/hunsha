package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.entity.Package;
import com.hunsha.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "后台-套餐管理")
@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
public class AdminPackageController {

    private final PackageService packageService;

    @Operation(summary = "获取套餐列表（分页）")
    @GetMapping
    public Result<PageResult<Package>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Package> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Package::getName, name);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Package::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Package::getStatus, status);
        }
        wrapper.orderByAsc(Package::getSortOrder).orderByDesc(Package::getCreatedAt);
        Page<Package> pageResult = packageService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "新增套餐")
    @PostMapping
    public Result<Void> create(@RequestBody Package pkg) {
        packageService.save(pkg);
        return Result.ok();
    }

    @Operation(summary = "更新套餐")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Package pkg) {
        pkg.setId(id);
        packageService.updateById(pkg);
        return Result.ok();
    }

    @Operation(summary = "删除套餐")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        packageService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "上下架套餐")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Package pkg = new Package();
        pkg.setId(id);
        pkg.setStatus(body.get("status"));
        packageService.updateById(pkg);
        return Result.ok();
    }
}
