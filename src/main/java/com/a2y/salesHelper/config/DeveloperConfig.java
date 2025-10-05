package com.a2y.salesHelper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeveloperConfig {

    @Value("${developer.token:dev-secret-2024}")
    private String developerToken;

    @Value("${developer.secret:developer-secret-key}")
    private String developerSecret;

    @Value("${developer.enabled:true}")
    private boolean developerEnabled;

    // Getters for use in DeveloperController
    public String getDeveloperToken() {
        return developerToken;
    }

    public String getDeveloperSecret() {
        return developerSecret;
    }

    public boolean isDeveloperEnabled() {
        return developerEnabled;
    }
}
