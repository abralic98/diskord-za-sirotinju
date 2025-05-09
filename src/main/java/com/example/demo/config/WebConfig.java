package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Define CORS settings for GraphQL endpoint
        registry.addMapping("/graphql")  // You can target specific paths
                .allowedOrigins("http://localhost:3000")  // Frontend URL
                .allowedMethods("GET", "POST")  // Allow specific HTTP methods
                .allowedHeaders("*")  // Allow any headers
                .allowCredentials(true);  // Allow credentials such as cookies or authorization headers
    }
}
