package com.triangle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI triangleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Triangle API")
                        .description("REST API for managing triangles and calculating their properties")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Triangle API Team")
                                .email("api@triangle.com")));
    }
}