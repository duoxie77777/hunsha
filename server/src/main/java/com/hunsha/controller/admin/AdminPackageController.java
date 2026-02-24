package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-套餐管理")
@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
public class AdminPackageController {

    private final PackageService packageService;

    @Operation(summary = "获取套餐列表（分页）")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "新增套餐")
    @PostMapping
    public Result<?> create() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "更新套餐")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "删除套餐")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "上下架套餐")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
