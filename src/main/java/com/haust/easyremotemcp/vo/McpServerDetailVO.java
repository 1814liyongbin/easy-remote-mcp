package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/12 14:59
 * @description: mcp server详情
 */
@Data
public class McpServerDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "密钥")
    private String secretKey;

    @Schema(description = "工具信息")
    private List<ToolVO> tools;

    @Schema(description = "mcp服务url")
    private String serverUrl;

    public McpServerDetailVO(List<ToolVO> tools) {
        this.tools = tools;
    }
}
