package com.bildungsinsitut.deutschkurse.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // This method creates a RestTemplate bean that can be injected elsewhere
        // in your application for making HTTP requests.
        return new RestTemplate();
    }

    // You can define other beans here
}