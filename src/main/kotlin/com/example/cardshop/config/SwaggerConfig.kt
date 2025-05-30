package com.example.cardshop.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Card Shop API")
                .version("1.0")
                .description("API documentation for Card Shop backend.")
        )

    @Bean
    fun publicApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/**")
        .build()
} 