package com.haust.easyremotemcp.service;

import com.haust.easyremotemcp.entity.Tool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haust.easyremotemcp.vo.ToolVO;

import java.util.List;

/**
* @author liyongbin
* @description 针对表【tool(工具信息)】的数据库操作Service
* @createDate 2025-04-11 12:02:41
*/
public interface ToolService extends IService<Tool> {

    List<ToolVO> getByServerId(Long id);

    void removeByServerId(Long id);

    void doSave(List<ToolVO> tools, Long id);
}
