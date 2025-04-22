package com.haust.easyremotemcp.controller;

import com.haust.easyremotemcp.request.ServerListRequest;
import com.haust.easyremotemcp.service.McpServerService;
import com.haust.easyremotemcp.vo.McpServerDetailVO;
import com.haust.easyremotemcp.vo.McpServerListVO;
import com.haust.easyremotemcp.vo.ResponseInfo;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "获取mcp server列表")
    public ResponseInfo<List<McpServerListVO>> list(@RequestBody ServerListRequest request) {
        return ResponseInfo.ok(mcpServerService.doList(request));
    }

    @GetMapping("/server/detail")
    @Operation(summary = "获取mcp server详情")
    public ResponseInfo<McpServerDetailVO> detail(@RequestParam("id") String id, @RequestParam("secretKey") String secretKey) {
        return mcpServerService.detail(id,secretKey);
    }

    @PostMapping("/server/save")
    @Operation(summary = "保存mcp server")
    public ResponseInfo<String> save(@RequestBody McpServerDetailVO detailVO) {
        return mcpServerService.doSave(detailVO);
    }

    @GetMapping("/server/delete")
    @Operation(summary = "删除mcp server")
    public ResponseInfo<String> delete(@RequestParam("id") String id, @RequestParam("secretKey") String secretKey) {
        return mcpServerService.doDelete(id, secretKey);
    }

    @GetMapping("/server/validate")
    @Operation(summary = "验证mcp server")
    public ResponseInfo<String> validate(@RequestParam("id") String id, @RequestParam("secretKey") String secretKey){
        return mcpServerService.doValidate(id, secretKey);
    }
}
