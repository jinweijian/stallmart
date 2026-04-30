package com.stallmart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StallMart API")
                        .description("StallMart SaaS 后端 API 文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("StallMart Dev Team")
                                .email("dev@stallmart.com")));
    }
}
