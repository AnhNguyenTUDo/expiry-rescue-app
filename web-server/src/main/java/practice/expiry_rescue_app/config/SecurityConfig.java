package practice.expiry_rescue_app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import practice.expiry_rescue_app.config.security.OAuth2AuthenticationFailureHandler;
import practice.expiry_rescue_app.config.security.CustomOAuth2UserService;
import practice.expiry_rescue_app.config.security.JwtAuthenticationFilter;
import practice.expiry_rescue_app.config.security.OAuth2AuthenticationSuccessHandler;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CORS configuration
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Disable CSRF (we're using JWT tokens)
                                .csrf(csrf -> csrf.disable())

                                // Stateless session management (JWT-based)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Allow CORS preflight requests
                                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                                                .permitAll()

                                                // Public endpoints
                                                .requestMatchers(
                                                                "/",
                                                                "/error",
                                                                "/favicon.ico",
                                                                "/auth/**",
                                                                "/oauth2/**")
                                                .permitAll()

                                                // Public API endpoints - explicitly allow GET requests for browsing
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/v1/supermarkets",
                                                                "/api/v1/supermarkets/active",
                                                                "/api/v1/supermarkets/{id}",
                                                                "/api/v1/supermarkets/{id}/with-products",
                                                                "/api/v1/cities",
                                                                "/api/v1/cities/{cityId}/districts")
                                                .permitAll()

                                                // Public product endpoints - explicitly allow GET requests for browsing
                                                // products
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/v1/products/inventory",
                                                                "/api/v1/products/inventory/{id}",
                                                                "/api/v1/products/inventory/supermarket/{supermarketId}",
                                                                "/api/v1/products/inventory/product-master/{productMasterId}",
                                                                "/api/v1/products/inventory/status/{status}",
                                                                "/api/v1/products/inventory/expiring",
                                                                "/api/v1/products/master",
                                                                "/api/v1/products/master/{id}",
                                                                "/api/v1/products/master/category/{categoryId}",
                                                                "/api/v1/product-categories",
                                                                "/api/v1/product-categories/{id}")
                                                .permitAll()

                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // OAuth2 Login configuration
                                .oauth2Login(oauth2 -> oauth2
                                                // Custom user service to handle OAuth2 user info
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                // Success handler - generates JWT and redirects to frontend
                                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                                // Failure handler - redirects to frontend with error
                                                .failureHandler(oAuth2AuthenticationFailureHandler))

                                // Exception handling - return 401 instead of redirecting to login
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        // Return 401 Unauthorized for all unauthenticated requests
                                                        // This prevents automatic redirects to login page
                                                        response.setContentType("application/json");
                                                        response.setStatus(
                                                                        jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.getWriter().write(
                                                                        "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                                                }))

                                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * CORS configuration to allow requests from frontend
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow frontend origin
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

                // Allow common HTTP methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                // Allow all headers
                configuration.setAllowedHeaders(Arrays.asList("*"));

                // Allow credentials (cookies, authorization headers)
                configuration.setAllowCredentials(true);

                // Expose headers that frontend can access
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type"));

                // Max age for preflight requests (1 hour)
                configuration.setMaxAge(3600L);

                // Apply CORS configuration to all endpoints
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * Password encoder for hashing passwords
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
