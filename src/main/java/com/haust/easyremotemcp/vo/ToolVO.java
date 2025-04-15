package com.haust.easyremotemcp.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/12 15:19
 * @description:
 */
@Data
public class ToolVO {
    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "server id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long serverId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "url")
    private String url;

    @Schema(description = "方法")
    private String method;

    @Schema(description = "参数")
    private List<ToolParamVO> toolParam;
}
