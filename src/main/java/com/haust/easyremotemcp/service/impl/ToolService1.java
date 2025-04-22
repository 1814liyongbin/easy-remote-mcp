package com.haust.easyremotemcp.service.impl;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * @author: liyongbin
 * @date: 2025/4/13 14:03
 * @description:
 */
@Service
public class ToolService1 {

    @Tool(description = "查询天气")
    public String getCityWeather(@ToolParam(description = "城市名称") String city, @ToolParam(description = "日期 示例格式 2025-01-01") String date) {
        return city + " " + date + "雨天";
    }
}
