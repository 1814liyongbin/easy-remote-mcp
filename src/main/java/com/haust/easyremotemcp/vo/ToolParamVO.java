package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: liyongbin
 * @date: 2025/4/12 15:26
 * @description: 工具入参
 */
public record ToolParamVO(@Schema(description = "工具id")
                         @JsonSerialize(using = ToStringSerializer.class)
                         Long toolId,

                         @Schema(description = "参数名")
                         String name,

                         @Schema(description = "参数描述")
                         String description) {

}
