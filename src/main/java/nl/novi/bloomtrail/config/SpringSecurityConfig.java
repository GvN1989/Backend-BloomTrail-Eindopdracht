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

                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{username}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/{username}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{username}/authority").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{username}/authorities").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/{username}/authorities").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/{username}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/{username}/profile-picture").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users/{username}/profile-picture").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/{username}/profile-picture").authenticated()

                        .requestMatchers(HttpMethod.GET, "/coaching-programs/summary").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/{name}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/user/{username}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/coaching-programs/{id}/progress").authenticated()
                        .requestMatchers(HttpMethod.POST, "/coaching-programs").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/coaching-programs/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/coaching-programs/{username}/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/step/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/step/{username}/{programId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/step/{programId}/step").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.POST, "/step/{programId}/steps-batch").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.PUT, "/step/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.DELETE, "/step/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.GET, "/step/{id}/download-zip").authenticated()

                        .requestMatchers(HttpMethod.GET, "/session/user/{username}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/session").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.PUT, "/session/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.POST, "/session/{id}/client-reflection").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST, "/session/{id}/coach-notes").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.DELETE, "/session/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.GET, "/session/{id}/download-zip").authenticated()

                        .requestMatchers(HttpMethod.GET, "/strength-results/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/strength-results/user/{username}").hasAnyAuthority("ADMIN", "COACH")
                        .requestMatchers(HttpMethod.POST, "/strength-results").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/strength-results").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/strength-results/user/{username}").hasAnyAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/assignment/step/{stepId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/assignment").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.PUT, "/assignment/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.DELETE, "/assignment/{id}").hasAnyAuthority("ADMIN","COACH")
                        .requestMatchers(HttpMethod.GET, "/assignment/{id}/download-zip").authenticated()

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
