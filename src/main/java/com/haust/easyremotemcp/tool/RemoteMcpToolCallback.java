package com.haust.easyremotemcp.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.haust.easyremotemcp.vo.ToolVO;
import lombok.Builder;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author: liyongbin
 * @date: 2025/4/12 19:26
 * @description: RemoteMcpToolCallback
 */
@Builder
public class RemoteMcpToolCallback implements ToolCallback {

    private ToolDefinition toolDefinition;

    private ToolMetadata toolMetadata;

    private ToolVO toolVO;

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
        JSONObject entries = JSONUtil.parseObj(toolInput);
        if (StrUtil.equals(HttpMethod.GET.name(), toolVO.getMethod())) {
            StringBuilder url = new StringBuilder(toolVO.getUrl());
            if (!entries.isEmpty()) {
                url.append("?");
                for (Map.Entry entry : entries.entrySet()) {
                    url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                url = new StringBuilder(url.substring(0, url.length() - 1));
            }
            return HttpUtil.get(url.toString());
        } else {
            return HttpUtil.post(toolVO.getUrl(), entries.toString());
        }
    }

    @Override
    public String call(String toolInput, ToolContext tooContext) {
        return call(toolInput);
    }
}
