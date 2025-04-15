package com.haust.easyremotemcp.mapper;

import com.haust.easyremotemcp.entity.Tool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author liyongbin
* @description 针对表【tool(工具信息)】的数据库操作Mapper
* @createDate 2025-04-11 12:02:41
* @Entity com.haust.easyremotemcp.entity.Tool
*/
@Mapper
public interface ToolMapper extends BaseMapper<Tool> {

}




