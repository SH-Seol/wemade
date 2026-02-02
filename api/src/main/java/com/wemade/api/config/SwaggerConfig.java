package com.wemade.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.application.version}")
    private String appVersion;

    @Value("${spring.application.description}")
    private String appDescription;

    @Value("${spring.application.contact}")
    private String appContact;

    @Value("${app.swagger.description}")
    private String appSwaggerDescription;

    @Value("${app.swagger.github-url}")
    private String githubUrl;


    @Bean
    public OpenAPI openAPI(){

        Info info = new Info().title(appName)
                .version(appVersion)
                .description(appDescription)
                .contact(new Contact().name(appContact)
                );

        return new OpenAPI()
                .info(info)
                .externalDocs(new ExternalDocumentation()
                        .description(appSwaggerDescription)
                        .url(githubUrl)
                );
    }

}
