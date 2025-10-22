package com.a2y.salesHelper.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("A2Y Sales Helper")
                                                .version("1.0.0")
                                                .description("API for Sales Helper Application")
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                                .components(new Components()
                                                .addSecuritySchemes(
                                                                "developer-auth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.APIKEY)
                                                                                .in(SecurityScheme.In.HEADER)
                                                                                .name("X-Developer-Token")
                                                                                .description("Developer authentication token - Internal use only")))
                                .servers(List.of(
                                                new Server().url("http://69.62.77.235:8080")
                                                                .description("Development server")));
        }
}
