package com.haust.easyremotemcp.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.haust.easyremotemcp.request.SearchNearbyRequest;
import com.haust.easyremotemcp.util.MultiResourceRateLimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

/**
 * @author: liyongbin
 * @date: 2025/4/20 20:51
 * @description: 简单接口示例
 */
@RestController
@Slf4j
public class SimpleDemoController {

    @Autowired
    private Environment environment;

    @Autowired
    private MultiResourceRateLimiterUtil multiResourceRateLimiterUtil;

    @GetMapping("/district")
    public String district(@RequestParam("keywords") String keywords) {
        log.info("查询行政区域:{}", keywords);
        if (!multiResourceRateLimiterUtil.tryAcquire("district")) {
            log.info("查询行政区域已到达最大次数:{}", keywords);
            return "对不起，已达到当日请求最大次数";
        }
        return HttpUtil.get("https://restapi.amap.com/v3/config/district?keywords=" + keywords + "&subdistrict=1&extensions=base&key=" + environment.getProperty("apiKey"));
    }

    @PostMapping("/searchNearby")
    public String searchNearby(@RequestBody SearchNearbyRequest request) {
        log.info("查询附近搜索:{}", JSONUtil.toJsonStr(request));
        if (StrUtil.isBlank(request.getKeywords())) {
            return "关键字不能为空";
        }
        if (StrUtil.isBlank(request.getRegion())) {
            return "召回区域不能为空";
        }
        if (!multiResourceRateLimiterUtil.tryAcquire("request")) {
            log.info("查询附近搜索已到达最大次数:{}", JSONUtil.toJsonStr(request));
            return "对不起，已达到当日请求最大次数";
        }
        return HttpUtil.get("https://restapi.amap.com/v5/place/text?keywords=" + request.getKeywords() + "&region=" + request.getRegion() + "&key=" + environment.getProperty("apiKey"));
    }
}
