package com.github.adaken4.lets_play.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.github.adaken4.lets_play.security.AuthTokenFilter;
import com.github.adaken4.lets_play.security.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Password encoder bean used for hashing user passwords
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Exposes AuthenticationManager so it can be injected (e.g. into services/controllers)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // JWT / token filter that runs before UsernamePasswordAuthenticationFilter
    @Autowired
    private AuthTokenFilter authTokenFilter;

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF because this is a stateless REST API using tokens
            .csrf(csrf -> csrf.disable())
            // Do not create HTTP sessions; rely on JWT/token for each request
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Customize handling of unauthorized (401) and forbidden (403) responses
            .exceptionHandling(ex -> ex
                // Called when a user is authenticated but not allowed to access a resource
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden"))
                // Called when a request has no valid authentication
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"))
            )
            // Configure URL-based authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        // Register the custom token filter before the username/password filter
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    // Bean definition for a custom AccessDeniedHandler (for use if you wire it explicitly)
    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
