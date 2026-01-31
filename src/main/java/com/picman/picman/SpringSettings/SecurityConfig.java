package com.picman.picman.SpringSettings;

import com.picman.picman.SpringAuthentication.JwtAuthFilter;
import com.picman.picman.SpringAuthentication.UserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    private     final   JwtAuthFilter   authFilter;
    private     final   Logger          logger;


    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // PAY MUCH ATTENTION TO URLs

                        // General mappings and resources
                        .requestMatchers("/**", "/u/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/bootstrap/**", "/imgs/**").permitAll()

                        // Content access
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/cn/**").permitAll()
                        .requestMatchers("/cn/dashboard").permitAll()
                        .requestMatchers("/cn/dashboard/submitSearchQuery").permitAll()
                        .requestMatchers("/cn/home").authenticated()

                        // Images management
                        .requestMatchers( "/cn/i/edit", "/cn/i/upload").hasAnyAuthority("o", "w")
                        .requestMatchers( "/cn/i/delete").hasAnyAuthority("o", "d")

                        // Categories management
                        .requestMatchers( "/cn/c/**").hasAnyAuthority("o", "w", "d")
                        .requestMatchers( "/cn/c/create","/cn/c/edit").hasAnyAuthority("o", "w")
                        .requestMatchers( "/cn/c/delete").hasAnyAuthority("o", "d")

                        // Any
                        .requestMatchers("/h2-console").hasAnyAuthority("o")

                        // Deny any other request
                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8088"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            logger.warn("[SECURITY] Access denied for {} {}: {}", request.getMethod(), request.getRequestURI(),
                    accessDeniedException.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        };
    }

}
