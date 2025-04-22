package com.haust.easyremotemcp.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: liyongbin
 * @date: 2025/4/16 22:09
 * @description: 服务器列表请求
 */
@Data
public class ServerListRequest {
    @Schema(description = "服务器名称")
    private String name;
}
