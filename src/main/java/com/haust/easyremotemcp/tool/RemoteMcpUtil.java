package com.haust.easyremotemcp.tool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import com.haust.easyremotemcp.vo.ToolParamVO;
import com.haust.easyremotemcp.vo.ToolVO;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.ai.util.json.schema.SpringAiSchemaModule;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liyongbin
 * @date: 2025/4/14 15:53
 * @description:
 */
public class RemoteMcpUtil {

    private static final boolean PROPERTY_REQUIRED_BY_DEFAULT = true;

    private static final SchemaGenerator TYPE_SCHEMA_GENERATOR;

    private static final SchemaGenerator SUBTYPE_SCHEMA_GENERATOR;

    static {
        com.github.victools.jsonschema.generator.Module jacksonModule = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
        com.github.victools.jsonschema.generator.Module openApiModule = new Swagger2Module();
        Module springAiSchemaModule = PROPERTY_REQUIRED_BY_DEFAULT ? new SpringAiSchemaModule()
                : new SpringAiSchemaModule(SpringAiSchemaModule.Option.PROPERTY_REQUIRED_FALSE_BY_DEFAULT);

        SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                .with(jacksonModule)
                .with(openApiModule)
                .with(springAiSchemaModule)
                .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .with(Option.PLAIN_DEFINITION_KEYS);

        SchemaGeneratorConfig typeSchemaGeneratorConfig = schemaGeneratorConfigBuilder.build();
        TYPE_SCHEMA_GENERATOR = new SchemaGenerator(typeSchemaGeneratorConfig);

        SchemaGeneratorConfig subtypeSchemaGeneratorConfig = schemaGeneratorConfigBuilder
                .without(Option.SCHEMA_VERSION_INDICATOR)
                .build();
        SUBTYPE_SCHEMA_GENERATOR = new SchemaGenerator(subtypeSchemaGeneratorConfig);
    }

    public static List<RemoteMcpToolCallback> convertToolCallback(List<ToolVO> toolVOS) {
        return toolVOS.stream().map(toolVO -> RemoteMcpToolCallback.builder()
                .toolVO(toolVO)
                .toolDefinition(new DefaultToolDefinition(toolVO.getName(), toolVO.getDescription(), generateForMethodInput(toolVO.getToolParam())))
                .toolMetadata(new DefaultToolMetadata(false))
                .build()).toList();
    }

    public static String generateForMethodInput(List<ToolParamVO> toolParam) {
        ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
        schema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
        schema.put("type", "object");

        ObjectNode properties = schema.putObject("properties");
        List<String> required = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(toolParam)) {
            for (ToolParamVO toolParamVO : toolParam) {
                String parameterName = toolParamVO.getName();
                Type parameterType = String.class;
                if (ObjectUtil.isNotEmpty(toolParamVO.getRequired()) && toolParamVO.getRequired().equals(1)) {
                    required.add(parameterName);
                }
                ObjectNode parameterNode = SUBTYPE_SCHEMA_GENERATOR.generateSchema(parameterType);
                String parameterDescription = toolParamVO.getDescription();
                if (StringUtils.hasText(parameterDescription)) {
                    parameterNode.put("description", parameterDescription);
                }
                properties.set(parameterName, parameterNode);
            }
        }
        var requiredArray = schema.putArray("required");
        required.forEach(requiredArray::add);
        return schema.toPrettyString();
    }

    public static String getServerUrl(String serverUrl, String randomStr) {
        return "http://" + serverUrl + "/" + randomStr + "/sse";
    }
}
