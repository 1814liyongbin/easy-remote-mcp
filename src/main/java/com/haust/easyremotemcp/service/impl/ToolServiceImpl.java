package com.haust.easyremotemcp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haust.easyremotemcp.entity.Tool;
import com.haust.easyremotemcp.entity.ToolParam;
import com.haust.easyremotemcp.mapper.ToolMapper;
import com.haust.easyremotemcp.service.ToolParamService;
import com.haust.easyremotemcp.service.ToolService;
import com.haust.easyremotemcp.vo.ToolParamVO;
import com.haust.easyremotemcp.vo.ToolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liyongbin
 * @description 针对表【tool(工具信息)】的数据库操作Service实现
 * @createDate 2025-04-11 12:02:41
 */
@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, Tool>
        implements ToolService {

    @Autowired
    private ToolParamService toolParamService;

    @Override
    public List<ToolVO> getByServerId(Long id) {
        List<ToolVO> toolVOS;
        List<Tool> tools = list(Wrappers.lambdaQuery(Tool.class)
                .eq(Tool::getServerId, id));
        if (CollectionUtil.isEmpty(tools)) {
            return Collections.emptyList();
        }
        List<Long> ids = tools.stream().map(Tool::getId).toList();
        List<ToolParam> params = toolParamService.list(Wrappers.lambdaQuery(ToolParam.class)
                .in(ToolParam::getToolId, ids));
        toolVOS = BeanUtil.copyToList(tools, ToolVO.class);
        if (CollectionUtil.isNotEmpty(params)) {
            Map<Long, List<ToolParam>> toolParamMap = params.stream().collect(Collectors.groupingBy(ToolParam::getToolId));
            for (ToolVO toolVO : toolVOS) {
                toolVO.setToolParam(BeanUtil.copyToList(toolParamMap.get(toolVO.getId()), ToolParamVO.class));
            }
        }
        return toolVOS;
    }

    @Override
    public void removeByServerId(Long id) {
        List<Tool> tools = list(Wrappers.lambdaQuery(Tool.class)
                .eq(Tool::getServerId, id));
        if (CollectionUtil.isNotEmpty(tools)) {
            List<Long> ids = tools.stream().map(Tool::getId).toList();
            update(Wrappers.lambdaUpdate(Tool.class)
                    .in(Tool::getId, ids)
                    .set(Tool::getIsDeleted, 1)
                    .set(Tool::getUpdateTime, DateUtil.now()));
            toolParamService.update(Wrappers.lambdaUpdate(ToolParam.class)
                    .in(ToolParam::getToolId, ids)
                    .set(ToolParam::getIsDeleted, 1)
                    .set(ToolParam::getUpdateTime, DateUtil.now()));
        }
    }

    @Override
    public void doSave(List<ToolVO> tools, Long id) {
        for (ToolVO toolVO : tools) {
            Tool tool = BeanUtil.toBean(toolVO, Tool.class);
            tool.setCreateTime(DateUtil.now());
            tool.setUpdateTime(DateUtil.now());
            tool.setServerId(id);
            save(tool);
            if (CollectionUtil.isNotEmpty(toolVO.getToolParam())) {
                List<ToolParam> toolParams = new ArrayList<>(toolVO.getToolParam().size());
                for (ToolParamVO toolParamVO : toolVO.getToolParam()) {
                    ToolParam toolParam = BeanUtil.toBean(toolParamVO, ToolParam.class);
                    toolParam.setToolId(tool.getId());
                    toolParam.setRequired(toolParamVO.getRequired());
                    toolParam.setCreateTime(DateUtil.now());
                    toolParam.setUpdateTime(DateUtil.now());
                    toolParams.add(toolParam);
                }
                toolParamService.saveBatch(toolParams);
            }
        }
    }
}




