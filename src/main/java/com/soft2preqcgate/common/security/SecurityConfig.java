package com.soft2preqcgate.common.security;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

/**
 * soft2cost2와 동일한 정책으로 구성한다.
 * - 무상태(STATELESS): 세션을 만들지 않고 요청마다 토큰을 검증한다.
 * - 인증 도메인 구현 시 RedisTokenFilter를 만들어
 *   .addFilterBefore(redisTokenFilter, UsernamePasswordAuthenticationFilter.class) 로 체인에 끼운다.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final String allowedOrigin;
    private final boolean swaggerEnabled;
    private final int bcryptStrength;

    public SecurityConfig(
            @Value("${soft2preqcgate.cors.allowed-origins[0]}") String allowedOrigin,
            @Value("${soft2preqcgate.security.swagger-enabled:false}") boolean swaggerEnabled,
            @Value("${soft2preqcgate.auth.login.bcrypt-strength:8}") int bcryptStrength
    ) {

        if (allowedOrigin == null || allowedOrigin.isBlank()) {
            throw new IllegalStateException("At least one CORS origin is required");
        }

        if (bcryptStrength < 8 || bcryptStrength > 14) {
            throw new IllegalStateException("bcrypt-strength must be between 8 and 14");
        }

        this.allowedOrigin = allowedOrigin;
        this.swaggerEnabled = swaggerEnabled;
        this.bcryptStrength = bcryptStrength;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API는 헤더 토큰 기반이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // Frontend/Backend 포트가 다르므로 CORS 적용
                .cors(Customizer.withDefaults())

                // 세션을 만들지 않는 무상태 모드
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {
                    auth
                            // 스프링 부트 기본 에러 처리 dispatch(/error) 허용
                            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    auth
                            // 헬스체크는 인증 없이 허용
                            .requestMatchers("/api/v1/health").permitAll();

                    if (swaggerEnabled) {
                        auth
                                .requestMatchers(  // Swagger UI
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**").permitAll();
                    }

                    auth
                            .anyRequest().authenticated();
                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Nexacro Port 9091 허용. (Cross Origin Resource Sharing)
        corsConfiguration.setAllowedOrigins(
                List.of(
                        allowedOrigin
                )
        );

        corsConfiguration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        corsConfiguration.setAllowedHeaders(
                List.of(
                        "Content-Type",
                        "X-Auth-Token",
                        "Cache-Control",
                        "Pragma",
                        "Expires",
                        "If-Modified-Since"
                )
        );

        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }
}
