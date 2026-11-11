package com.example.AtlasRH.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AtlasRH - API REST Gestion des Ressources Humaines")
                        .version("1.0.0")
                        .description("Documentation complète de l'API REST AtlasRH pour la gestion des départements, " +
                                "employés, contrats, demandes et soldes de congés.")
                        .contact(new Contact()
                                .name("Équipe Développement AtlasRH")
                                .email("contact@atlasrh.ma"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
