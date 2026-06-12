package com.acme.cms.security;

import com.acme.cms.security.model.UserStatus;
import com.acme.cms.security.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, HeaderUserFilter headerUserFilter) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/health", "/actuator/health", "/openapi/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(headerUserFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    HeaderUserFilter headerUserFilter(UserRepository users, @Value("${app.security.default-user-email}") String defaultEmail) {
        return new HeaderUserFilter(users, defaultEmail);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(Lists.CORS_METHODS);
        configuration.setAllowedHeaders(Lists.CORS_HEADERS);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    static class HeaderUserFilter extends OncePerRequestFilter {
        private final UserRepository users;
        private final String defaultEmail;

        HeaderUserFilter(UserRepository users, String defaultEmail) {
            this.users = users;
            this.defaultEmail = defaultEmail;
        }

        @Override
        protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
        ) throws ServletException, IOException {
            SecurityContextHolder.clearContext();
            String path = request.getRequestURI();
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.equals("/api/v1/health")
                || path.equals("/actuator/health")
                || path.startsWith("/openapi/")) {
                filterChain.doFilter(request, response);
                return;
            }
            String email = request.getHeader("X-User-Email");
            if (email == null || email.isBlank()) {
                email = defaultEmail;
            }

            users.findByEmailIgnoreCase(email)
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .ifPresent(user -> {
                    var authorities = user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .distinct()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                    var authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "header", authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

            filterChain.doFilter(request, response);
        }
    }

    private static final class Lists {
        private static final java.util.List<String> CORS_METHODS = java.util.List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS");
        private static final java.util.List<String> CORS_HEADERS = java.util.List.of("Authorization", "Content-Type", "X-User-Email");
    }
}
