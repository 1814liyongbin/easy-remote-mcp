package com.haust.easyremotemcp.controller;

import com.haust.easyremotemcp.service.McpServerService;
import com.haust.easyremotemcp.vo.McpServerDetailVO;
import com.haust.easyremotemcp.vo.McpServerListVO;
import com.haust.easyremotemcp.vo.ResponseInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/12 08:38
 * @description: mcp server相关接口
 */
@RestController
@Tag(name = "mcp server相关接口")
@CrossOrigin
public class McpServerController {

    @Autowired
    private McpServerService mcpServerService;

    @PostMapping("/server/list")
    public ResponseInfo<List<McpServerListVO>> list() {
        return ResponseInfo.ok(mcpServerService.doList());
    }

    @GetMapping("/server/detail")
    public ResponseInfo<McpServerDetailVO> detail(@RequestParam("id") String id) {
        return ResponseInfo.ok(mcpServerService.detail(id));
    }

    @PostMapping("/server/save")
    public ResponseInfo<String> save(@RequestBody McpServerDetailVO detailVO) {
        return mcpServerService.doSave(detailVO);
    }
}
