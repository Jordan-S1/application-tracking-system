package com.ats;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application class for the Job Application Tracking System.
 * Configures and runs the Spring Boot application.
 * Also sets up OpenAPI documentation with JWT bearer authentication.
 */
@SpringBootApplication
public class JobApplicationTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplicationTrackerApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .info(new Info()
                        .title("Job Application Tracking System API")
                        .version("1.0.0")
                        .description(
                                "Full-stack ATS for managing job applications with JWT authentication and role-based access control"));
    }
}