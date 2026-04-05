package com.grocery.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //disable csrf for APIs
                .cors(Customizer.withDefaults()) //Enable CORS integration
                .csrf(csrf -> csrf.disable())
                // Defining which endpoint are public and which protected
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/products/**", "/api/categories/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/ws-grocery/**").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated())
                //tell spring boot not create any session (we use jwt instead)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        //Add our JWT filter before Spring's default Usernamepassword filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow your Frontend URL
        configuration.setAllowedOrigins(allowedOrigins);
        // Allow standard HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // CRITICAL: Allow your custom header 'user-payload' and standard headers
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "user-payload"));

        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
    //Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Authentication Manager Bean (needed Spring internally)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Define max file size (e.g., 5MB)
        factory.setMaxFileSize(DataSize.ofMegabytes(10L));
        // Define max request size (sum of all files/data, e.g., 10MB)
        factory.setMaxRequestSize(DataSize.ofMegabytes(10L));
        // This ensures the Servlet container knows how to handle large files and multi-part data
        return factory.createMultipartConfig();
    }

    // Add the MultipartResolver Bean (necessary for the filter to work)
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }


    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartFilterRegistration() {
        // 1. Create the standard MultipartFilter
        MultipartFilter multipartFilter = new MultipartFilter();
        // This tells the filter to use the StandardServletMultipartResolver bean you defined
        multipartFilter.setMultipartResolverBeanName("multipartResolver");
        // 2. Register the filter and set a very low order number for absolute priority
        FilterRegistrationBean<MultipartFilter> registration = new FilterRegistrationBean<>(multipartFilter);
        registration.addUrlPatterns("/api/admin/products/*"); // Apply to the admin products path
        // CRITICAL: Set the order to the lowest possible value (highest priority)
        // -100 or less ensures it runs before Spring Security's FilterChainProxy.
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }
}






