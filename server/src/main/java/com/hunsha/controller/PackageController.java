package com.hunsha.controller;

import com.hunsha.common.Result;
import com.hunsha.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "套餐接口")
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @Operation(summary = "获取套餐列表")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "获取套餐详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
