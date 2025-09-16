package com.library.library_backend.config;

import com.library.library_backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS включен (бин corsConfigurationSource в CorsConfig)
                .cors(Customizer.withDefaults())
                // CSRF не нужен для REST
                .csrf(csrf -> csrf.disable())
                // Stateless API
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // кого пускаем без токена
                .authorizeHttpRequests(auth -> auth
                        // preflight-запросы браузера
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // health/info (если нужно для мониторинга)
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // публичная аутентификация
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll()

                        // публичные GET (оставил как было у тебя)
                        .requestMatchers(HttpMethod.GET,
                                "/l/**",
                                "/api/books/qr/**",
                                "/api/qr/**",
                                "/api/books/**"
                        ).permitAll()

                        // остальное — только с JWT
                        .anyRequest().authenticated()
                );

        // Важно: JWT фильтр до UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
