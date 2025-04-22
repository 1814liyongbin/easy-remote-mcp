package com.haust.easyremotemcp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haust.easyremotemcp.entity.McpServer;
import com.haust.easyremotemcp.request.ServerListRequest;
import com.haust.easyremotemcp.vo.McpServerDetailVO;
import com.haust.easyremotemcp.vo.McpServerListVO;
import com.haust.easyremotemcp.vo.ResponseInfo;

import java.util.List;

/**
* @author liyongbin
* @description 针对表【mcp_server】的数据库操作Service
* @createDate 2025-04-11 12:02:41
*/
public interface McpServerService extends IService<McpServer> {

    List<McpServerListVO> doList(ServerListRequest request);

    ResponseInfo<McpServerDetailVO> detail(String id,String secretKey);

    ResponseInfo<String> doSave(McpServerDetailVO detailVO);

    ResponseInfo<String> doDelete(String id,String secretKey);

    ResponseInfo<String> doValidate(String id, String secretKey);
}
