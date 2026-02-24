package com.hunsha.controller;

import com.hunsha.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "批量上传图片")
    @PostMapping("/images")
    public Result<?> uploadImages(@RequestParam("files") MultipartFile[] files) {
        // TODO
        return Result.ok();
    }
}
