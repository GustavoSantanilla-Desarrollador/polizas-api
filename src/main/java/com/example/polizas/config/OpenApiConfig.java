package com.example.polizas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "apiKey";

    @Bean
    public OpenAPI polizasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestion de Polizas")
                        .description("Prueba tecnica: gestion de polizas de arrendamiento y sus riesgos")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("x-api-key")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }
}
