package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/12 14:59
 * @description: mcp server详情
 */
public record McpServerDetailVO(@JsonSerialize(using = ToStringSerializer.class)
                                @Schema(description = "id")
                                Long id,

                                @Schema(description = "名称")
                                String name,

                                @Schema(description = "图片url")
                                String imgUrl,

                                @Schema(description = "服务器url")
                                String serverUrl,

                                @Schema(description = "描述")
                                String description,

                                @Schema(description = "工具信息")
                                List<ToolVO> tools) {

    public McpServerDetailVO(List<ToolVO> tools) {
        this(null, null, null, null, null, tools);
    }
}
