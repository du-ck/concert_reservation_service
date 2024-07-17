package com.hh.consertreservation.support.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }
    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new Info()
                .title("콘서트신청 서비스")
                .description("콘서트 신청")
                .version("1.0.0");
    }
}