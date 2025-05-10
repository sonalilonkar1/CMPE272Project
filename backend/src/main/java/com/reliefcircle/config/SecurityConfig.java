package com.reliefcircle.config;

import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**", "/api/authenticate").permitAll()
                .requestMatchers("/api/proofs/**").permitAll()
                // Base /api/charities endpoint is public, but endpoints with parameters need authentication
                .requestMatchers(HttpMethod.GET, "/api/charities").permitAll()
                // Special handling for /api/charities with query parameters
                .requestMatchers(HttpMethod.GET, "/api/charities/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/charities/verified").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/charities/{id}").permitAll()

                // Charity operations
                .requestMatchers(HttpMethod.POST, "/api/charities").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers(HttpMethod.PUT, "/api/charities/*").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers(HttpMethod.PUT, "/api/charities/*/verify").hasAuthority("ROLE_FUNDRAISER")

                // User and donation endpoints
                .requestMatchers("/api/users/donors/volunteers").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers("/api/users").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers("/api/users/{userId}/image/**").authenticated()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/donations/*/verify").hasAuthority("ROLE_FUNDRAISER")
                
                // Donation endpoints
                .requestMatchers(HttpMethod.GET, "/api/donations").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers(HttpMethod.POST, "/api/donations").hasAnyAuthority("ROLE_DONOR", "ROLE_FUNDRAISER")
                .requestMatchers(HttpMethod.GET, "/api/donations/{id}").hasAnyAuthority("ROLE_DONOR", "ROLE_FUNDRAISER")

                // Fundraiser-only endpoints
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_FUNDRAISER")
                
                // Default authenticated access for other API endpoints
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(userDetailsService(), passwordEncoder()))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
