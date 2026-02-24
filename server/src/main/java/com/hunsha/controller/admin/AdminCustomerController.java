package com.hunsha.controller.admin;

import com.hunsha.common.Result;
import com.hunsha.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-客户管理")
@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final UserService userService;

    @Operation(summary = "获取客户列表（分页）")
    @GetMapping
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
