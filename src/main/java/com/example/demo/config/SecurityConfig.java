package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

  @Bean
  DefaultSecurityFilterChain springWebFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(c -> c.disable())
        // Demonstrate that method security works
        // Best practice to use both for defense in depth
        .authorizeRequests(requests -> requests.anyRequest().authenticated())
        .httpBasic(withDefaults())
        .build();
  }



  @Bean
  public static InMemoryUserDetailsManager users() {
    User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
    UserDetails notAuthorized = userBuilder.username("rob").password("rob")
        .roles(SECURITY_ROLES.UNAUTHORIZED.toString()).build();
    UserDetails admin = userBuilder.username("admin").password("admin")
        .roles(SECURITY_ROLES.ADMIN.toString(), SECURITY_ROLES.MEMBER.toString()).build();
    UserDetails member = userBuilder.username("member").password("member").roles("MEMBER").build();
    return new InMemoryUserDetailsManager(notAuthorized, admin, member);
  }

}
