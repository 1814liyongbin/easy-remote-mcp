package com.haust.easyremotemcp.vo;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Data
public class ResponseInfo<T> {
    // 开放字段，用于定时任务成功与否匹配
    public static final int ERROR = 500;
    public static final int OK = 200;
    /**
     * 请求是否成功
     */
    @Schema(description = "请求是否成功")
    private boolean success;
    /**
     * 响应码
     */
    @Schema(description = "响应码")
    private int code;
    /**
     * 返回信息
     */
    @Schema(description = "返回信息")
    private String msg;
    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T data;

    public static <T> ResponseInfo<T> ok() {
        return ResponseInfo.<T>builder().success(true).code(200).build();
    }

    public static <T> ResponseInfo<T> ok(int code) {
        return ResponseInfo.<T>builder().success(true).code(code == 0 ? 200 : code).build();
    }

    public static <T> ResponseInfo<T> ok(String msg) {
        return ResponseInfo.<T>builder().success(true).code(200).msg(msg).build();
    }

    public static <T> ResponseInfo<T> ok(T data) {
        return ResponseInfo.<T>builder().success(true).code(200).data(data).build();
    }

    public static <T> ResponseInfo<T> ok(String msg, T data) {
        return ResponseInfo.<T>builder().success(true).code(200).msg(StrUtil.isNotBlank(msg) ? msg : null).data(data).build();
    }

    public static <T> ResponseInfo<T> ok(T data, String msg, int code) {
        return ResponseInfo.<T>builder().success(true).code(code == 0 ? 200 : code).msg(StrUtil.isNotBlank(msg) ? msg : null).data(data).build();
    }


    public static <T> ResponseInfo<T> error(int code, String msg) {
        boolean notBlank = StrUtil.isNotBlank(msg);
        if (notBlank) {
            log.warn(msg);
        }
        return ResponseInfo.<T>builder().success(false).code(code).msg(notBlank ? msg : "对不起，请稍后重试！").build();
    }

    public static <T> ResponseInfo<T> error(String msg) {
        boolean notBlank = StrUtil.isNotBlank(msg);
        if (notBlank) {
            log.warn(msg);
        }
        return ResponseInfo.<T>builder().success(false).code(ERROR).msg(notBlank ? msg : "对不起，请稍后重试！").build();
    }

    public static <T> ResponseInfo<T> error(int code, String msg, T data) {
        boolean notBlank = StrUtil.isNotBlank(msg);
        if (notBlank) {
            log.warn(msg);
        }
        return ResponseInfo.<T>builder().success(false).code(code).msg(notBlank ? msg : "对不起，请稍后重试！").data(data).build();
    }
}

