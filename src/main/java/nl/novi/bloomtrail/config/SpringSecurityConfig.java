package nl.novi.bloomtrail.config;

import nl.novi.bloomtrail.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import nl.novi.bloomtrail.services.CustomUserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SpringSecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }
    @Bean
    protected SecurityFilterChain filter (HttpSecurity http, CustomAuthenticationEntryPoint authEntryPoint) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authenticated").authenticated()

                        .requestMatchers(HttpMethod.POST, "/users/").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{username}/authorities").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/{username}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{username}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()

                        .requestMatchers(HttpMethod.POST, "/coaching-programs").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/coaching-programs/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/steps", "/sessions", "/assignments").hasAnyAuthority("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.DELETE, "/steps/**", "/sessions/**","/assignments/**").hasAnyAuthority("ADMIN", "COACH")

                        .requestMatchers(HttpMethod.POST, "/session-insights", "/strength-results").hasAnyAuthority("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/session-insights/**", "/strength-results/**").hasAnyAuthority("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.GET, "/session-insights/**", "/strength-results/**").hasAnyAuthority("ADMIN", "COACH", "USER")

                        .requestMatchers(HttpMethod.GET, "/coaching-programs", "/steps").hasAnyAuthority("ADMIN", "COACH", "USER")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/{id}", "/steps/{id}", "/sessions/{id}", "/assignments/{id}").hasAnyAuthority("ADMIN", "COACH")

                        .anyRequest().authenticated()
                )

                        .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(authEntryPoint)
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



}
