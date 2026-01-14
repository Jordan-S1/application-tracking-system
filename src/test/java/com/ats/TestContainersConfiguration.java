package com.ats;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration for setting up Testcontainers with PostgreSQL.
 */
@TestConfiguration
public class TestContainersConfiguration {

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:18-alpine"))
                .withDatabaseName("ats_test_db")
                .withUsername("ats_user")
                .withPassword("ats_password");

        container.start();
        return container;
    }
}