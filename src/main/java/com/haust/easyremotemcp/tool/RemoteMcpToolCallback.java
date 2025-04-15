package com.haust.easyremotemcp.tool;

import lombok.Builder;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * @author: liyongbin
 * @date: 2025/4/12 19:26
 * @description: RemoteMcpToolCallback
 */
@Builder
public class RemoteMcpToolCallback implements ToolCallback {

    private ToolDefinition toolDefinition;
    private ToolMetadata toolMetadata;

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return toolMetadata;
    }

    @Override
    public String call(String toolInput) {
        return "";
    }

    @Override
    public String call(String toolInput, ToolContext tooContext) {
        return ToolCallback.super.call(toolInput, tooContext);
    }
}
