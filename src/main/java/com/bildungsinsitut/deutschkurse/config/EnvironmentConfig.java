package com.bildungsinsitut.deutschkurse.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@Configuration
@Getter
@Slf4j
public class EnvironmentConfig {

    private final Environment environment;

    // Database Configuration
    @Value("${DB_URL:#{null}}")
    private String databaseUrl;

    @Value("${DB_USERNAME:#{null}}")
    private String databaseUsername;

    @Value("${DB_PASSWORD:#{null}}")
    private String databasePassword;

    // JWT Configuration
    @Value("${JWT_SECRET:#{null}}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION:86400000}")
    private Long jwtExpiration;

    // Application Configuration
    @Value("${SERVER_PORT:8080}")
    private Integer serverPort;

    @Value("${SPRING_PROFILES_ACTIVE:default}")
    private String activeProfile;

    public EnvironmentConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateConfiguration() {
        log.info("Validating environment configuration...");

        // Validate required environment variables
        validateRequiredVariable("DB_PASSWORD", databasePassword);
        validateRequiredVariable("JWT_SECRET", jwtSecret);

        // Validate JWT secret strength
        if (jwtSecret != null && jwtSecret.length() < 32) {
            log.warn("JWT_SECRET should be at least 32 characters long for security");
        }

        // Log non-sensitive configuration
        log.info("Active profile: {}", activeProfile);
        log.info("Server port: {}", serverPort);
        log.info("Database URL: {}", maskSensitiveUrl(databaseUrl));
        log.info("JWT expiration: {} ms", jwtExpiration);

        log.info("Environment configuration validation completed");
    }

    private void validateRequiredVariable(String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Required environment variable %s is not set", name)
            );
        }
    }

    private String maskSensitiveUrl(String url) {
        if (url == null) return "Not set";

        // Mask password in database URL if present
        return url.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }

    /**
     * Check if we're running in production
     */
    public boolean isProduction() {
        return "prod".equals(activeProfile) || "production".equals(activeProfile);
    }

    /**
     * Check if we're running in development
     */
    public boolean isDevelopment() {
        return "dev".equals(activeProfile) || "development".equals(activeProfile);
    }

    /**
     * Get environment variable with fallback
     */
    public String getEnvVar(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     * Get required environment variable (throws exception if not found)
     */
    public String getRequiredEnvVar(String key) {
        String value = environment.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Required environment variable " + key + " is not set");
        }
        return value;
    }
}