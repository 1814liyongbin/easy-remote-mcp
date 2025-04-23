package com.haust.easyremotemcp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haust.easyremotemcp.entity.McpServer;
import com.haust.easyremotemcp.entity.Tool;
import com.haust.easyremotemcp.entity.ToolParam;
import com.haust.easyremotemcp.handler.RemoteMcpHandlerMapping;
import com.haust.easyremotemcp.service.ToolParamService;
import com.haust.easyremotemcp.service.ToolService;
import com.haust.easyremotemcp.service.impl.McpServerServiceImpl;
import com.haust.easyremotemcp.tool.RemoteMcpToolCallback;
import com.haust.easyremotemcp.tool.RemoteMcpUtil;
import com.haust.easyremotemcp.vo.ToolParamVO;
import com.haust.easyremotemcp.vo.ToolVO;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haust.easyremotemcp.service.impl.McpServerServiceImpl.*;

@SpringBootApplication
@Slf4j
public class EasyRemoteMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyRemoteMcpApplication.class, args);
    }


    /**
     * 启动时初始化数据库中已存在的mcp server
     * @param mcpServerService
     * @param toolService
     * @param toolParamService
     * @param remoteMcpHandlerMapping
     * @return
     */
    @Bean
    public CommandLineRunner commandLineRunner(McpServerServiceImpl mcpServerService,
                                               ToolService toolService,
                                               ToolParamService toolParamService,
                                               RemoteMcpHandlerMapping remoteMcpHandlerMapping) {
        return args -> {
            List<McpServer> list = mcpServerService.list(Wrappers.lambdaQuery(McpServer.class));
            if (CollectionUtil.isNotEmpty(list)) {
                List<Long> ids = list.stream().map(McpServer::getId).toList();
                Map<Long, List<Tool>> toolMap = toolService.list(Wrappers.lambdaQuery(Tool.class)
                        .in(Tool::getServerId, ids)).stream().collect(Collectors.groupingBy(Tool::getServerId));
                for (McpServer server : list) {
                    List<Tool> tools = toolMap.get(server.getId());
                    List<ToolParam> paramList = toolParamService.list(Wrappers.lambdaQuery(ToolParam.class)
                            .in(ToolParam::getToolId, tools.stream().map(Tool::getId).toList()));
                    List<ToolVO> toolVOS = BeanUtil.copyToList(tools, ToolVO.class);
                    if (CollectionUtil.isNotEmpty(paramList)) {
                        Map<Long, List<ToolParam>> paramMap = paramList.stream().collect(Collectors.groupingBy(ToolParam::getToolId));
                        for (ToolVO toolVO : toolVOS) {
                            toolVO.setToolParam(BeanUtil.copyToList(paramMap.get(toolVO.getId()), ToolParamVO.class));
                        }
                    }
                    WebFluxSseServerTransportProvider transportProvider = new WebFluxSseServerTransportProvider(new ObjectMapper(), "", "/" + server.getRandomStr() + "/sse/message", "/" + server.getRandomStr() + "/sse");
                    McpSchema.Implementation serverInfo = new McpSchema.Implementation(server.getName(), "1.0.0");
                    io.modelcontextprotocol.server.McpServer.AsyncSpecification serverBuilder = io.modelcontextprotocol.server.McpServer.async(transportProvider).serverInfo(serverInfo);
                    List<RemoteMcpToolCallback> remoteMcpToolCallbacks = RemoteMcpUtil.convertToolCallback(toolVOS);
                    List<McpServerFeatures.AsyncToolSpecification> asyncToolSpecification = toAsyncToolSpecification(remoteMcpToolCallbacks);
                    serverBuilder.tools(asyncToolSpecification);
                    McpSchema.ServerCapabilities build = McpSchema.ServerCapabilities.builder().tools(true).build();
                    serverBuilder.capabilities(build);
                    McpAsyncServer asyncServer = serverBuilder.build();
                    serverMap.put(server.getRandomStr(), asyncServer);
                    toolSpecificationMap.put(server.getRandomStr(), asyncToolSpecification);
                    remoteMcpHandlerMapping.addMapping(server.getRandomStr(), transportProvider.getRouterFunction());
                    log.info("mcp server start success, name: {} randomStr: {}", server.getName(), server.getRandomStr());
                }
            }
        };
    }
}
