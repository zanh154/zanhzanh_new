package com.fptu.swp391.se1839.oemevwarrantymanagement.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class SecurityConfig {

    // private final String[] PUBLIC_ENDPOINTS = {
    // "/api/temp-file/{filename}",
    // "/api/customers",
    // "/api/dashboard/summary",
    // "/api/models", "/api/model/detail/{id}",
    // "/api/categories", "/api/parts", "/api/categories/parts",
    // "/api/partInventories", "/api/partInventory/serviceCenter/{id}",
    // "/api/partPolicies", "/api/partPolicies/{id}", "/api/partPolicy",
    // "/api/partPolicy/code",
    // "/api/policies", "/api/policy/{policyId}", "/api/policy",
    // "/api/repairDetails/{id}",
    // "/api/repairOrders", "/api/repairOrders/{id}",
    // "/api/repairSteps/{id}",
    // "/api/campaigns", "/api/campaigns/{id}",
    // "/api/servicecenters",
    // "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh",
    // "/auth/user", "/auth/users/{userID}",
    // "/auth/users", "/auth/users/inactive/{userID}", "/auth/users/search",
    // "/auth/users/active/{userID}",
    // "/auth/change-password", "/ạuth/forgot-password",
    // "/api/claims/vehicle/{phone}",
    // "/api/claims", "/api/claims/{id}"
    // };

    private CustomerJwtDecoder customerJwtDecoder;

    @Value("${SIGN_KEY}")
    private String SIGN_KEY;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                }) // hoặc cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/token", "/auth/refresh", "/auth/introspect", "/auth/forgot-password")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customerJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/dashboard", true));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.ngrok-free.dev"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
