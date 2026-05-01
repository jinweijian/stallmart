package com.stallmart.support.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StallMart API")
                        .description("StallMart 服务端 API 文档")
                        .version("0.1.0")
                        .contact(new Contact().name("StallMart Dev Team")));
    }
}
