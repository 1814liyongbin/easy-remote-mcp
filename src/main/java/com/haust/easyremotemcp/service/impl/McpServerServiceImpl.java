package com.haust.easyremotemcp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haust.easyremotemcp.entity.McpServer;
import com.haust.easyremotemcp.handler.RemoteMcpHandlerMapping;
import com.haust.easyremotemcp.mapper.McpServerMapper;
import com.haust.easyremotemcp.service.McpServerService;
import com.haust.easyremotemcp.service.ToolService;
import com.haust.easyremotemcp.tool.RemoteMcpToolCallback;
import com.haust.easyremotemcp.tool.RemoteMcpUtil;
import com.haust.easyremotemcp.vo.McpServerDetailVO;
import com.haust.easyremotemcp.vo.McpServerListVO;
import com.haust.easyremotemcp.vo.ResponseInfo;
import com.haust.easyremotemcp.vo.ToolVO;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liyongbin
 * @description 针对表【mcp_server】的数据库操作Service实现
 * @createDate 2025-04-11 12:02:41
 */
@Service
@Slf4j
public class McpServerServiceImpl extends ServiceImpl<McpServerMapper, McpServer>
        implements McpServerService {

    @Autowired
    private ToolService toolService;

    @Autowired
    private RemoteMcpHandlerMapping remoteMcpHandlerMapping;

    @Override
    public List<McpServerListVO> doList() {
        List<McpServer> list = list(Wrappers.lambdaQuery(McpServer.class)
                .orderByDesc(McpServer::getCreateTime));
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(list, McpServerListVO.class);
    }

    @Override
    public McpServerDetailVO detail(String id) {
        McpServer mcpServer = getById(Long.valueOf(id));
        if (ObjectUtil.isEmpty(mcpServer)) {
            return null;
        }
        List<ToolVO> tools = toolService.getByServerId(Long.valueOf(id));
        McpServerDetailVO detailVO = new McpServerDetailVO(tools);
        CglibUtil.copy(mcpServer, detailVO);
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseInfo<String> doSave(McpServerDetailVO detailVO) {
//        McpServer mcpServer = CglibUtil.copy(detailVO, McpServer.class);
//        mcpServer.setUpdateTime(DateUtil.now());
        String randomStr = RandomUtil.randomString(6);
        System.out.println("randomStr:" + randomStr);
//        if (ObjectUtil.isEmpty(mcpServer.getId())) {
//            mcpServer.setCreateTime(DateUtil.now());
//        }
//        saveOrUpdate(mcpServer);
//        toolService.removeByServerId(mcpServer.getId());
//        if (ObjectUtil.isNotEmpty(detailVO.tools())) {
//            toolService.doSave(detailVO.tools(), mcpServer.getId());
//        }
        WebFluxSseServerTransportProvider transportProvider = new WebFluxSseServerTransportProvider(new ObjectMapper(), "", "/" + randomStr + "/sse/message", "/" + randomStr + "/sse");
        McpSchema.Implementation serverInfo = new McpSchema.Implementation(detailVO.name(), "1.0.0");
        io.modelcontextprotocol.server.McpServer.AsyncSpecification serverBuilder = io.modelcontextprotocol.server.McpServer.async(transportProvider).serverInfo(serverInfo);
        List<RemoteMcpToolCallback> remoteMcpToolCallbacks = RemoteMcpUtil.convertToolCallback(detailVO.tools());
        List<McpServerFeatures.AsyncToolSpecification> asyncToolSpecification = toAsyncToolSpecification(remoteMcpToolCallbacks);
        serverBuilder.tools(asyncToolSpecification);
        McpSchema.ServerCapabilities build = McpSchema.ServerCapabilities.builder().tools(true).build();
        serverBuilder.capabilities(build);
        serverBuilder.build();
        remoteMcpHandlerMapping.addMapping(randomStr, transportProvider.getRouterFunction());
        return ResponseInfo.ok();
    }

    private List<McpServerFeatures.AsyncToolSpecification> toAsyncToolSpecification(List<RemoteMcpToolCallback> tools) {
        return tools.stream()
                .collect(Collectors.toMap(tool -> tool.getToolDefinition().name(),
                        tool -> tool,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .map(tool -> McpToolUtils.toAsyncToolSpecification(tool, null))
                .toList();
    }
}