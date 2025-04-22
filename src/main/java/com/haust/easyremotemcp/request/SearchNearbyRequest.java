package com.haust.easyremotemcp.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: liyongbin
 * @date: 2025/4/21 18:02
 * @description: 搜索附近请求
 */
@Data
public class SearchNearbyRequest {
    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "召回区域")
    private String region;
}
