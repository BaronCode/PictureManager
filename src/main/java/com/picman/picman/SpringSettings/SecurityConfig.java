package com.picman.picman.SpringSettings;

import com.picman.picman.LoggingMgmt.Log;
import com.picman.picman.LoggingMgmt.LogServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtAuthFilter;
import com.picman.picman.SpringAuthentication.UserDetailsService;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;


@Configuration
public class SecurityConfig {

    private     final   JwtAuthFilter   authFilter;
    private     final   Logger          logger;
    private     final LogServiceImplementation logService;
    private     final UserServiceImplementation userService;


    public SecurityConfig(JwtAuthFilter authFilter, LogServiceImplementation lsi, UserServiceImplementation usi) {
        this.authFilter = authFilter;
        logger = LoggerFactory.getLogger(this.getClass());
        logService = lsi;
        userService = usi;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // PAY MUCH ATTENTION TO URLs

                        // General mappings and resources
                        .requestMatchers("/u/**", "/error", "/wip", "/contacts", "/pricing").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/bootstrap/**", "/imgs/**", "/favicon.ico").permitAll()

                        // Error endpoints
                        .requestMatchers("/_401", "/_403").permitAll()

                        // Content access
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/cn/gallery").permitAll()
                        .requestMatchers("/cn/home").authenticated()

                        // Images management
                        .requestMatchers("/cn/i/**").hasAnyAuthority("o", "s", "w", "d")
                        .requestMatchers( "/cn/i/edit","/cn/i/tagedit", "/cn/i/upload").hasAnyAuthority("o", "s", "w")
                        .requestMatchers( "/cn/i/delete").hasAnyAuthority("o", "s", "d")

                        // Categories management
                        .requestMatchers( "/cn/c/**").hasAnyAuthority("o", "s", "w", "d")
                        .requestMatchers( "/cn/c/create","/cn/c/edit").hasAnyAuthority("o", "s", "w")
                        .requestMatchers( "/cn/c/delete").hasAnyAuthority("o", "s", "d")

                        // Admin
                        .requestMatchers("/cn/admin/**").hasAnyAuthority("o", "u")

                        // Any
                        .requestMatchers("/h2-console", "/h2-console/**").hasAnyAuthority("o")

                        // Deny any other request
                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
/*                .formLogin(login -> login
                        .loginPage("/u/login")
                        .loginProcessingUrl("/u/loginsubmit")
                        .defaultSuccessUrl("/cn/gallery", true)
                )*/
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User u = userService.findByEmail(auth.getName());
            Log log = new Log(LocalDateTime.now(), request.getRequestURI(), "SecurityConfig", u, "Access denied");
            logService.save(log);
            request.getRequestDispatcher("/_403").forward(request, response);
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authenticationException) -> {
            Log log = new Log(LocalDateTime.now(), request.getRequestURI(), "SecurityConfig", null, "Unauthenticated access");
            logService.save(log);
            request.getRequestDispatcher("/_401").forward(request, response);
        };
    }

}
