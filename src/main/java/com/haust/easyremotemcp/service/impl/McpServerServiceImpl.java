package com.haust.easyremotemcp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haust.easyremotemcp.entity.McpServer;
import com.haust.easyremotemcp.handler.RemoteMcpHandlerMapping;
import com.haust.easyremotemcp.mapper.McpServerMapper;
import com.haust.easyremotemcp.request.ServerListRequest;
import com.haust.easyremotemcp.service.McpServerService;
import com.haust.easyremotemcp.service.ToolService;
import com.haust.easyremotemcp.tool.RemoteMcpToolCallback;
import com.haust.easyremotemcp.tool.RemoteMcpUtil;
import com.haust.easyremotemcp.vo.McpServerDetailVO;
import com.haust.easyremotemcp.vo.McpServerListVO;
import com.haust.easyremotemcp.vo.ResponseInfo;
import com.haust.easyremotemcp.vo.ToolVO;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private Environment environment;

    @Autowired
    private RemoteMcpHandlerMapping remoteMcpHandlerMapping;

    public static final Map<String, List<McpServerFeatures.AsyncToolSpecification>> toolSpecificationMap = new ConcurrentHashMap<>();

    public static final Map<String, McpAsyncServer> serverMap = new ConcurrentHashMap<>();

    @Override
    public List<McpServerListVO> doList(ServerListRequest request) {
        List<McpServer> list = list(Wrappers.lambdaQuery(McpServer.class)
                .like(StrUtil.isNotBlank(request.getName()), McpServer::getName, request.getName())
                .orderByDesc(McpServer::getCreateTime));
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(list, McpServerListVO.class);
    }

    @Override
    public ResponseInfo<McpServerDetailVO> detail(String id, String secretKey) {
        McpServer mcpServer = getById(Long.valueOf(id));
        if (ObjectUtil.isEmpty(mcpServer) || !StrUtil.equals(mcpServer.getSecretKey(), MD5.create().digestHex(secretKey))) {
            return ResponseInfo.error("密钥不正确");
        }
        List<ToolVO> tools = toolService.getByServerId(Long.valueOf(id));
        McpServerDetailVO detailVO = new McpServerDetailVO(tools);
        BeanUtil.copyProperties(mcpServer, detailVO);
        detailVO.setServerUrl(RemoteMcpUtil.getServerUrl(environment.getProperty("serverUrl"), mcpServer.getRandomStr()));
        return ResponseInfo.ok(detailVO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseInfo<String> doSave(McpServerDetailVO detailVO) {
        McpServer mcpServer;
        String randomStr;
        if (ObjectUtil.isNotEmpty(detailVO.getId())) {
            mcpServer = getById(detailVO.getId());
            if (!StrUtil.equals(mcpServer.getSecretKey(), MD5.create().digestHex(detailVO.getSecretKey()))) {
                return ResponseInfo.error("密钥不正确");
            }
            mcpServer.setName(detailVO.getName());
            mcpServer.setDescription(detailVO.getDescription());
            randomStr = mcpServer.getRandomStr();
        } else {
            mcpServer = BeanUtil.toBean(detailVO, McpServer.class);
            randomStr = RandomUtil.randomString(6);
            log.info("randomStr:" + randomStr);
            mcpServer.setRandomStr(randomStr);
            mcpServer.setCreateTime(DateUtil.now());
            mcpServer.setSecretKey(MD5.create().digestHex(detailVO.getSecretKey()));
        }
        mcpServer.setUpdateTime(DateUtil.now());
        saveOrUpdate(mcpServer);
        toolService.removeByServerId(mcpServer.getId());
        if (ObjectUtil.isNotEmpty(detailVO.getTools())) {
            toolService.doSave(detailVO.getTools(), mcpServer.getId());
        }
        if (ObjectUtil.isEmpty(mcpServer.getId()) || !serverMap.containsKey(randomStr)) {
            // 创建新的mcp server协议
            WebFluxSseServerTransportProvider transportProvider = new WebFluxSseServerTransportProvider(new ObjectMapper(), "", "/" + randomStr + "/sse/message", "/" + randomStr + "/sse");
            McpSchema.Implementation serverInfo = new McpSchema.Implementation(mcpServer.getName(), "1.0.0");
            io.modelcontextprotocol.server.McpServer.AsyncSpecification serverBuilder = io.modelcontextprotocol.server.McpServer.async(transportProvider).serverInfo(serverInfo);
            // 获取工具
            List<RemoteMcpToolCallback> remoteMcpToolCallbacks = RemoteMcpUtil.convertToolCallback(detailVO.getTools());
            List<McpServerFeatures.AsyncToolSpecification> asyncToolSpecification = toAsyncToolSpecification(remoteMcpToolCallbacks);
            // 为新的mcp server添加工具
            serverBuilder.tools(asyncToolSpecification);
            McpSchema.ServerCapabilities build = McpSchema.ServerCapabilities.builder().tools(true).build();
            serverBuilder.capabilities(build);
            McpAsyncServer asyncServer = serverBuilder.build();
            // 添加到map中 以便后续更新删除
            serverMap.put(randomStr, asyncServer);
            toolSpecificationMap.put(randomStr, asyncToolSpecification);
            // 添加新的mcp server路由
            remoteMcpHandlerMapping.addMapping(randomStr, transportProvider.getRouterFunction());
        } else {
            McpAsyncServer mcpAsyncServer = serverMap.get(randomStr);
            List<McpServerFeatures.AsyncToolSpecification> toolSpecificationList = toolSpecificationMap.get(randomStr);
            for (McpServerFeatures.AsyncToolSpecification toolSpecification : toolSpecificationList) {
                mcpAsyncServer.removeTool(toolSpecification.tool().name()).subscribe(v -> log.info("remove tool success {}", toolSpecification.tool().name()));
            }
            List<RemoteMcpToolCallback> remoteMcpToolCallbacks = RemoteMcpUtil.convertToolCallback(detailVO.getTools());
            List<McpServerFeatures.AsyncToolSpecification> asyncToolSpecification = toAsyncToolSpecification(remoteMcpToolCallbacks);
            for (McpServerFeatures.AsyncToolSpecification newTool : asyncToolSpecification) {
                mcpAsyncServer.addTool(newTool).subscribe(v -> log.info("addTool tool success {}", newTool.tool().name()));
            }
            mcpAsyncServer.notifyToolsListChanged().subscribe(v -> log.info("notifyToolsListChanged success {}", mcpAsyncServer.getServerInfo().name()));
            toolSpecificationMap.put(randomStr, asyncToolSpecification);
        }
        return ResponseInfo.ok("http://" + environment.getProperty("serverUrl") + "/" + randomStr + "/sse");
    }

    @Override
    public ResponseInfo<String> doDelete(String id, String secretKey) {
        McpServer mcpServer = getById(Long.valueOf(id));
        if (ObjectUtil.isEmpty(mcpServer) || !StrUtil.equals(mcpServer.getSecretKey(), MD5.create().digestHex(secretKey))) {
            return ResponseInfo.error("密钥不正确");
        }
        update(Wrappers.lambdaUpdate(McpServer.class)
                .eq(McpServer::getId, mcpServer.getId())
                .set(McpServer::getIsDeleted, 1)
                .set(McpServer::getUpdateTime, DateUtil.now()));
        clear(mcpServer.getRandomStr());
        return ResponseInfo.ok();
    }

    @Override
    public ResponseInfo<String> doValidate(String id, String secretKey) {
        McpServer mcpServer = getById(Long.valueOf(id));
        if (ObjectUtil.isEmpty(mcpServer) || !StrUtil.equals(mcpServer.getSecretKey(), MD5.create().digestHex(secretKey))) {
            return ResponseInfo.error("密钥不正确");
        }
        return ResponseInfo.ok();
    }


    public static List<McpServerFeatures.AsyncToolSpecification> toAsyncToolSpecification(List<RemoteMcpToolCallback> tools) {
        return tools.stream()
                .collect(Collectors.toMap(tool -> tool.getToolDefinition().name(),
                        tool -> tool,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .map(tool -> McpToolUtils.toAsyncToolSpecification(tool, null))
                .toList();
    }

    private void clear(String randomStr) {
        serverMap.remove(randomStr);
        toolSpecificationMap.remove(randomStr);
        remoteMcpHandlerMapping.clear(randomStr);
    }
}