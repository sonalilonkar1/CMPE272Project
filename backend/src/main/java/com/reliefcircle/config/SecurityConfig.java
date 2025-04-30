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
            .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/charities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/charities/verified").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/charities/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/charities").hasAuthority("ROLE_FUNDRAISER")
                .requestMatchers(HttpMethod.PUT, "/api/charities/*").hasAnyAuthority("ROLE_FUNDRAISER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/charities/*/verify").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/api/users/donors/volunteers").hasAnyAuthority("ROLE_FUNDRAISER", "ROLE_ADMIN")
                .requestMatchers("/api/users").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/api/users/{userId}/image/**").authenticated()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/donations/*/verify").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN")
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
