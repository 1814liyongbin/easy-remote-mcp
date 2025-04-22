package com.haust.easyremotemcp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName mcp_server
 */
@TableName(value ="mcp_server")
@Data
public class McpServer {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 随机字符串
     */
    private String randomStr;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 描述
     */
    private String description;

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