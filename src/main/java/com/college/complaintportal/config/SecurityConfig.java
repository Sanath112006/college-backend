package com.college.complaintportal.config;

import com.college.complaintportal.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/complaints/*/file"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
// package com.college.complaintportal.config;

// import com.college.complaintportal.security.JwtAuthenticationFilter;
// import com.college.complaintportal.security.RemoveWwwAuthenticateFilter;
// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.http.MediaType;
// import org.springframework.security.web.AuthenticationEntryPoint;
// import org.springframework.web.cors.CorsConfiguration;

// import jakarta.servlet.http.HttpServletResponse;
// import java.nio.charset.StandardCharsets;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.cors.CorsConfigurationSource;

// import java.util.List;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// @RequiredArgsConstructor
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     private static final String[] PUBLIC_URLS = {
//             "/api/auth/register",
//             "/api/auth/login"
//     };

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http
//                 .csrf(AbstractHttpConfigurer::disable)

//                 // Enable CORS for React frontend
//                 .cors(cors -> cors.configurationSource(corsConfigurationSource()))

//                 // Disable default Spring Security login popup
//                 .httpBasic(AbstractHttpConfigurer::disable)
//                 .formLogin(AbstractHttpConfigurer::disable)

//                 // Stateless session for JWT
//                 .sessionManagement(session ->
//                         session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//                 // Authorization rules
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers(PUBLIC_URLS).permitAll()
//                         .anyRequest().authenticated())

//                 // Return 401 JSON instead of WWW-Authenticate (stops browser login popup)
//                 .exceptionHandling(ex -> ex
//                         .authenticationEntryPoint(json401EntryPoint()))

//                 // Strip WWW-Authenticate from all responses first (prevents browser login popup)
//                 .addFilterBefore(new RemoveWwwAuthenticateFilter(), JwtAuthenticationFilter.class)
//                 // Add JWT filter
//                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     /** Returns 401 with JSON body and no WWW-Authenticate header so browser won't show login dialog. */
//     @Bean
//     public AuthenticationEntryPoint json401EntryPoint() {
//         return (request, response, authException) -> {
//             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//             response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//             response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//             String body = "{\"success\":false,\"message\":\"Unauthorized - invalid or missing token\",\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"}";
//             response.getWriter().write(body);
//         };
//     }

//     // Password Encoder
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // CORS configuration for React
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {

//         CorsConfiguration configuration = new CorsConfiguration();

//         configuration.setAllowedOrigins(List.of("http://localhost:3000"));
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(List.of("*"));
//         configuration.setAllowCredentials(true);

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);

//         return source;
//     }
// }

