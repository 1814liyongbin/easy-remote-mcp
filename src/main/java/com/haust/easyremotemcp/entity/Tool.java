package com.haust.easyremotemcp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工具信息
 * @TableName tool
 */
@TableName(value ="tool")
@Data
public class Tool {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * server id
     */
    private Long serverId;

    /**
     * 描述
     */
    private String description;

    /**
     * url
     */
    private String url;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 逻辑删除 0未删除 1已删除
     */
    @TableLogic
    private Integer isDeleted;
}