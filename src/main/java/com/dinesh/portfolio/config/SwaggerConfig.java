package com.dinesh.portfolio.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio Backend APIs")
                        .description("""
                                REST APIs for Portfolio Application
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Dinesh Kumawat")
                                .email("dkumawat7627@gmail.com")
                                .url("https://yourdomain.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.yourdomain.com")
                                .description("Production Server")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://yourdomain.com/docs"));
    }
}
