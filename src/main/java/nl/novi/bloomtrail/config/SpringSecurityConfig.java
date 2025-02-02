package nl.novi.bloomtrail.config;

import nl.novi.bloomtrail.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import nl.novi.bloomtrail.services.UserDetailsServiceImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final JwtRequestFilter jwtRequestFilter;

    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, JwtRequestFilter jwtRequestFilter, PasswordEncoder passwordEncoder) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    protected SecurityFilterChain filter (HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        // Wanneer je deze uncomments, staat je hele security open. Je hebt dan alleen nog een jwt nodig.
                        .requestMatchers("/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/coaching-programs").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/coaching-programs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/{id}").hasAnyRole("ADMIN", "COACH", "USER")

                        .requestMatchers(HttpMethod.POST, "/steps").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.DELETE, "/steps/**").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/steps").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/steps/{id}").hasAnyRole("ADMIN", "COACH", "USER")

                        .requestMatchers(HttpMethod.POST, "/sessions").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.DELETE, "/sessions/**").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/sessions").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/sessions/{id}").hasAnyRole("ADMIN", "COACH", "USER")

                        .requestMatchers(HttpMethod.POST, "/session-insights").hasAnyRole("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/session-insights/{id}").hasAnyRole("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.GET, "/session-insights").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/session-insights/{id}").hasAnyRole("ADMIN", "COACH", "USER")


                        .requestMatchers(HttpMethod.POST, "/strength-results").hasAnyRole("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/strength-results/{id}").hasAnyRole("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.GET, "/strength-results").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/strength-results/{id}").hasAnyRole("ADMIN", "COACH", "USER")


                        .requestMatchers(HttpMethod.POST, "/assignments").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.DELETE, "/assignments/**").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/assignments").hasAnyRole("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.GET, "/assignments/{id}").hasAnyRole("ADMIN", "COACH", "USER")

                        .requestMatchers("/authenticated").authenticated()
                        .requestMatchers("/authenticate").permitAll()
                        .anyRequest().denyAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource.\"}");
                        })
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
