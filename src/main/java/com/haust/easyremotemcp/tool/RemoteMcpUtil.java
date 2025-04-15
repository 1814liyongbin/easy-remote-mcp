package com.haust.easyremotemcp.tool;

import com.haust.easyremotemcp.vo.ToolVO;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;

import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/14 15:53
 * @description:
 */
public class RemoteMcpUtil {
    public static List<RemoteMcpToolCallback> convertToolCallback(List<ToolVO> toolVOS) {
        return toolVOS.stream().map(toolVO -> RemoteMcpToolCallback.builder()
                .toolDefinition(new DefaultToolDefinition(toolVO.getName(), toolVO.getDescription(), "{\"properties\": {},\"title\": \"list_indicesArguments\",\"type\": \"object\"}"))
                .toolMetadata(new DefaultToolMetadata(false))
                .build()).toList();
    }
}
