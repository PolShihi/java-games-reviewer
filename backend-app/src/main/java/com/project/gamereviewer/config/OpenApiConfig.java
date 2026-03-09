package com.project.gamereviewer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gamesReviewerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game Reviewer API")
                        .description("""
                                RESTful API for managing video games database with reviews, genres, 
                                production companies, and system requirements.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Game Reviewer Team")
                                .email("support@gamereviewer.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8088")
                                .description("Development server"),
                        new Server()
                                .url("https://api.gamereviewer.com")
                                .description("Production server")
                ));
    }
}
