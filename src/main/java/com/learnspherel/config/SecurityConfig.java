package com.learnspherel.config;

import com.learnspherel.entity.enums.VaiTro;
import com.learnspherel.filter.JwtAuthenticationFilter;
import com.learnspherel.service.CustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Value("${passwordencoder.strength}")
    private int passwordEncoderStrength;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, MessageSource messageSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.messageSource = messageSource;
    }

    /**
     * Configures the HTTP security settings including stateless sessions, authorization rules,
     * JWT filters, and exception handling.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the {@link SecurityFilterChain} to be used by Spring Security
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì sử dụng JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không sử dụng session
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**", "/signup", "/api/khoa-hoc/**", "/api/tai-lieu/**"
                                        , "/api/chuong-trinh", "/api/chuong-trinh/**", "/api/ky-nang", "/api/ky-nang/**"
                                        ,"/api/chuong-trinh/khoa-hoc/**","/api/thanh-toan/paypal/**","/api/bai-kiem-tra/**", "/api/danh-gia-khoa-hoc/**","api/ai-chat").permitAll() // Cho phép truy cập công khai
//                        .requestMatchers("/api/admin/**").hasAuthority(VaiTro.QUAN_TRI.name()) // Chỉ QUAN_TRI
                        .requestMatchers("/api/khoa-hoc/instructor-courses").hasAnyAuthority(VaiTro.GIANG_VIEN.name(), VaiTro.QUAN_TRI.name()) // GIANG_VIEN hoặc QUAN_TRI
                                .requestMatchers("/api/dang-ky-khoa-hoc/history").hasAnyRole(VaiTro.HOC_VIEN.name(),VaiTro.GIANG_VIEN.name(), VaiTro.QUAN_TRI.name()) // HOC_VIEN hoặc QUAN_TRI
                                .anyRequest().authenticated() // Tất cả các request khác cần xác thực
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Thêm filter JWT
                // Handle unauthorized access attempts
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );


        return http.build();
    }

    /**
     * Custom entry point for handling unauthorized access (401).
     */
    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            String message = messageSource.getMessage("auth.unauthorized", null, "Unauthorized access", null);
            response.getWriter().write("{\"success\": false, \"statusCode\": " + HttpStatus.UNAUTHORIZED.value() +
                    ", \"message\": \"" + message + "\", \"errorCode\": \"" + "UNAUTHORIZED" + "\", \"data\": null, \"timestamp\": " + System.currentTimeMillis() + "}");
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            String message = messageSource.getMessage("auth.forbidden", null, "Forbidden access", null);
            response.getWriter().write("{\"success\": false, \"statusCode\": " + HttpStatus.FORBIDDEN.value() +
                    ", \"message\": \"" + message + "\", \"errorCode\": \"" + "FORBIDDEN" + "\", \"data\": null, \"timestamp\": " + System.currentTimeMillis() + "}");
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordEncoderStrength);
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new CustomAuthenticationManager(userDetailsService, messageSource, passwordEncoder()
        );
    }
}