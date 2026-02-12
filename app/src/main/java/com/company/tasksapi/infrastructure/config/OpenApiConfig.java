package com.company.tasksapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI tasksApiOpenAPI() {
        Info info = new Info();
        info.setTitle("Tasks API");
        info.setDescription("Task Management API with Hexagonal Architecture");
        info.setVersion("1.0.0");
        
        Contact contact = new Contact();
        contact.setName("API Support");
        contact.setEmail("support@tasksapi.com");
        info.setContact(contact);
        
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");
        info.setLicense(license);
        
        return new OpenAPI().info(info);
    }
}
