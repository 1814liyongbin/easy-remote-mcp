package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: liyongbin
 * @date: 2025/4/12 09:12
 * @description: server返回实体
 */
public record McpServerListVO(@Schema(description = "id")
                              @JsonSerialize(using = ToStringSerializer.class)
                              Long id,

                              @Schema(description = "名称")
                              String name,

                              @Schema(description = "图片url")
                              String imgUrl,

                              @Schema(description = "描述")
                              String description,

                              @Schema(description = "创建时间")
                              String createTime) {}