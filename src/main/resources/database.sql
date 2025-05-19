CREATE TABLE `mcp_server` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `name` varchar(500) NOT NULL COMMENT '名称',
                              `random_str` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '随机字符串',
                              `secret_key` varchar(100) NOT NULL COMMENT '访问密钥',
                              `description` varchar(255) DEFAULT NULL COMMENT '描述',
                              `create_time` datetime NOT NULL COMMENT '创建时间',
                              `update_time` datetime NOT NULL COMMENT '更新时间',
                              `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='mcp服务信息';


CREATE TABLE `tool` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `server_id` bigint NOT NULL COMMENT 'server id',
                        `name` varchar(100) NOT NULL COMMENT '工具名称',
                        `description` varchar(500) NOT NULL COMMENT '描述',
                        `url` varchar(100) NOT NULL COMMENT 'url',
                        `method` varchar(10) NOT NULL COMMENT '请求方式',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '更新时间',
                        `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具信息';

CREATE TABLE `tool_param` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `tool_id` bigint DEFAULT NULL COMMENT '工具id',
                              `name` varchar(50) DEFAULT NULL COMMENT '名称',
                              `description` varchar(500) DEFAULT NULL COMMENT '描述',
                              `required` tinyint DEFAULT '0' COMMENT '是否是必填 1是 0否',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具参数信息';