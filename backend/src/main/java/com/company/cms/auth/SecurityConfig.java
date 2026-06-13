package com.company.cms.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MockPrincipalFilter mockPrincipalFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/health", "/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/openapi/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(mockPrincipalFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${cms.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-CMS-User", "X-CMS-Roles"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Component
    static class MockPrincipalFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthUser user = resolveUser(request);
                List<SimpleGrantedAuthority> authorities = user.roles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .toList();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, "N/A", authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }

        private AuthUser resolveUser(HttpServletRequest request) {
            String userHeader = request.getHeader("X-CMS-User");
            Set<RoleCode> roles = resolveRoles(request.getHeader("X-CMS-Roles"), userHeader);
            RoleCode primary = roles.iterator().next();
            return switch (primary) {
                case ADMIN -> new AuthUser(UUID.fromString("00000000-0000-0000-0000-000000000001"), "admin@example.com", "관리자", "Platform", roles);
                case EDITOR -> new AuthUser(UUID.fromString("00000000-0000-0000-0000-000000000002"), "editor@example.com", "콘텐츠 편집자", "Engineering", roles);
                case REVIEWER -> new AuthUser(UUID.fromString("00000000-0000-0000-0000-000000000003"), "reviewer@example.com", "검토자", "Security", roles);
                case VIEWER -> new AuthUser(UUID.fromString("00000000-0000-0000-0000-000000000005"), "viewer@example.com", "열람 사용자", "HR", roles);
                default -> new AuthUser(UUID.fromString("00000000-0000-0000-0000-000000000004"), "employee@example.com", "일반 사용자", "Engineering", roles);
            };
        }

        private Set<RoleCode> resolveRoles(String roleHeader, String userHeader) {
            LinkedHashSet<RoleCode> roles = new LinkedHashSet<>();
            if (roleHeader != null && !roleHeader.isBlank()) {
                for (String token : roleHeader.split(",")) {
                    parseRole(token).ifPresent(roles::add);
                }
            }
            if (roles.isEmpty() && userHeader != null) {
                parseRole(userHeader).ifPresent(roles::add);
            }
            if (roles.isEmpty()) {
                roles.add(RoleCode.EMPLOYEE);
            }
            if (!roles.contains(RoleCode.EMPLOYEE) && roles.stream().anyMatch(role -> role == RoleCode.ADMIN || role == RoleCode.EDITOR || role == RoleCode.REVIEWER)) {
                roles.add(RoleCode.EMPLOYEE);
            }
            return roles;
        }

        private java.util.Optional<RoleCode> parseRole(String value) {
            try {
                return java.util.Optional.of(RoleCode.valueOf(value.trim().toUpperCase()));
            } catch (RuntimeException ex) {
                return java.util.Optional.empty();
            }
        }
    }
}
