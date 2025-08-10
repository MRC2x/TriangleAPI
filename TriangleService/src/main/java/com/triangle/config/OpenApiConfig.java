package com.triangle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI triangleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Triangle API Service")
                        .description("A REST API service for managing triangles. Supports creating, retrieving, " +
                                   "deleting triangles and calculating their area and perimeter.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Triangle API Team")
                                .email("support@triangle-api.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("X-User"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("X-User", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-User")
                                .description("Authentication token for API access")));
    }
} 