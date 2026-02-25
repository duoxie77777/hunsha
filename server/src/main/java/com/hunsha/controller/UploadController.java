package com.hunsha.controller;

import com.hunsha.common.BusinessException;
import com.hunsha.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        validateImage(file);
        String url = saveFile(file);
        return Result.ok(url);
    }

    @Operation(summary = "批量上传图片")
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException("请选择要上传的文件");
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImage(file);
            urls.add(saveFile(file));
        }
        return Result.ok(urls);
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException("仅支持 JPG/PNG/GIF/WEBP 格式的图片");
        }
    }

    private String saveFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 按年月分目录存储
            Calendar cal = Calendar.getInstance();
            String subDir = cal.get(Calendar.YEAR) + "/" + String.format("%02d", cal.get(Calendar.MONTH) + 1);
            File dir = new File(uploadPath + subDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(dir, filename);
            file.transferTo(dest);

            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
