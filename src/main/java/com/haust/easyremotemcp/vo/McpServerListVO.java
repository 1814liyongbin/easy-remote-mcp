package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: liyongbin
 * @date: 2025/4/12 09:12
 * @description: server返回实体
 */
@Data
public class McpServerListVO {
    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private String createTime;
}