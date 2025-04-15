package com.haust.easyremotemcp;

import com.haust.easyremotemcp.service.impl.ToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EasyRemoteMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyRemoteMcpApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(ToolService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

}
