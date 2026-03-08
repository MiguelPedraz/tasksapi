package com.company.tasksapi.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tasksApiOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("https://api.tasksapi.com").description("Production")));
    }

    private Info apiInfo() {
        return new Info()
                .title("Tasks API")
                .description("Task Management REST API — Hexagonal Architecture, DDD, Spring Boot 3")
                .version("1.0.0")
                .contact(new Contact()
                        .name("API Support")
                        .email("support@tasksapi.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
