package com.company.cms.auth;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    public static final UUID ADMIN_ID = UUID.fromString("30000000-0000-0000-0000-000000000001");
    public static final UUID EDITOR_ID = UUID.fromString("30000000-0000-0000-0000-000000000002");
    public static final UUID REVIEWER_ID = UUID.fromString("30000000-0000-0000-0000-000000000003");
    public static final UUID EMPLOYEE_ID = UUID.fromString("30000000-0000-0000-0000-000000000004");
    public static final UUID HR_DEPARTMENT_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    public static final UUID ENGINEERING_DEPARTMENT_ID = UUID.fromString("10000000-0000-0000-0000-000000000002");

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/v1/health").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole(RoleCode.ADMIN.name())
                .requestMatchers("/api/v1/cms/**").hasAnyRole(
                    RoleCode.ADMIN.name(),
                    RoleCode.EDITOR.name(),
                    RoleCode.REVIEWER.name()
                )
                .requestMatchers("/api/v1/portal/**").authenticated()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${cms.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> {
            Map<String, Object> claims = demoClaims(token);
            if (claims.isEmpty()) {
                throw new JwtException("Unsupported local demo token.");
            }
            Instant now = Instant.now();
            return Jwt.withTokenValue(token)
                .header("alg", "none")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject((String) claims.get("email"))
                .claims(existing -> existing.putAll(claims))
                .build();
        };
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            Collection<GrantedAuthority> authorities = roles == null ? List.of() : roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .map(GrantedAuthority.class::cast)
                .toList();
            return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("email"));
        };
    }

    private Map<String, Object> demoClaims(String token) {
        String normalized = token == null ? "" : token.trim().toLowerCase();
        Map<String, Object> claims = new HashMap<>();
        switch (normalized) {
            case "admin" -> fill(claims, ADMIN_ID, "admin@example.com", "Admin User", ENGINEERING_DEPARTMENT_ID,
                List.of("ADMIN", "EMPLOYEE"));
            case "editor" -> fill(claims, EDITOR_ID, "editor@example.com", "Editor User", HR_DEPARTMENT_ID,
                List.of("EDITOR", "EMPLOYEE"));
            case "reviewer" -> fill(claims, REVIEWER_ID, "reviewer@example.com", "Reviewer User", HR_DEPARTMENT_ID,
                List.of("REVIEWER", "EMPLOYEE"));
            case "employee" -> fill(claims, EMPLOYEE_ID, "employee@example.com", "Employee User",
                ENGINEERING_DEPARTMENT_ID, List.of("EMPLOYEE"));
            default -> {
                return Map.of();
            }
        }
        return claims;
    }

    private void fill(Map<String, Object> claims, UUID id, String email, String name, UUID departmentId,
            List<String> roles) {
        claims.put("user_id", id.toString());
        claims.put("email", email);
        claims.put("name", name);
        claims.put("department_id", departmentId.toString());
        claims.put("roles", roles);
    }
}
