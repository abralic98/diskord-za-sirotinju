
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtUtil jwtUtil; // Inject JwtUtil into the SecurityConfig class

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Use Lambda DSL for cleaner and modern configuration
    http
        .csrf(csrf -> csrf.disable()) // Disable CSRF protection (if needed)
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable and configure CORS
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll() // Allow login endpoint
            .requestMatchers("/graphql").permitAll() // GraphQL will check inside resolver
            .requestMatchers("/graphiql", "/vendor/**", "/static/**").permitAll() // GraphiQL frontend
            .anyRequest().authenticated()) // Require authentication for other requests
        .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // Pass
                                                                                                            // JwtUtil
                                                                                                            // to the
                                                                                                            // filter

    return http.build();
  }

  // Create the CORS configuration source to be used in the Lambda DSL
  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Frontend URL
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST")); // Allowed HTTP methods
    corsConfig.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
    corsConfig.setAllowCredentials(true); // Allow credentials (cookies or authorization headers)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig); // Apply CORS config globally

    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
