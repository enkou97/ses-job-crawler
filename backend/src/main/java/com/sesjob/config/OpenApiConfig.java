package com.sesjob.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SES Job Crawler API")
                        .version("1.0.0")
                        .description("SES/フリーランス案件情報収集システムのREST API")
                        .contact(new Contact()
                                .name("SES Job Crawler")
                                .email("admin@example.com")));
    }
}
