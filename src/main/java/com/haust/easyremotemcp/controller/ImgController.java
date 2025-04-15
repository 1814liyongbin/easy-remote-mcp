package com.haust.easyremotemcp.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.haust.easyremotemcp.vo.ResponseInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: liyongbin
 * @date: 2025/4/12 08:51
 * @description: 图片相关接口
 */
@Tag(name = "图片相关接口")
@RestController
@CrossOrigin
public class ImgController {

    @PostMapping("/img/upload")
    @Operation(summary = "上传图片")
    public ResponseInfo<String> img(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseInfo.error("文件不能为空");
        }
        String extension = FileNameUtil.getSuffix(file.getOriginalFilename());
        if (!StrUtil.equals(extension, "jpg") && !StrUtil.equals(extension, "jpeg") && !StrUtil.equals(extension, "png")) {
            return ResponseInfo.error("文件格式不支持，仅支持 jpg、jpeg、png 格式");
        }
        String fileName = System.currentTimeMillis() + "." + extension;
        try {
            Files.copy(file.getInputStream(), Paths.get("src/main/resources/img/", fileName));
            return ResponseInfo.ok("http://127.0.0.1:8080/" + fileName);
        } catch (IOException e) {
            return ResponseInfo.error("文件保存失败：" + e.getMessage());
        }
    }
}