package com.reliefcircle.config;

import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
            userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found")
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->
                auth
                    // Public endpoints
                        .requestMatchers("/api/auth/**", "/api/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/charities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/charities/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/charities/{id}").permitAll()
                        .requestMatchers("/api/updates/charity/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/proofs/charity/{charityId}").permitAll() // Fix for API 20
                        // Authenticated endpoints
                        .requestMatchers(HttpMethod.GET, "/api/charities/**").authenticated()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/donations").authenticated() // Fix for API 16
                        .requestMatchers(HttpMethod.POST, "/api/donations").authenticated() // Fix for API 17
                        // Fundraiser endpoints
                        .requestMatchers(HttpMethod.POST, "/api/charities").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers(HttpMethod.PUT, "/api/charities/*").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers(HttpMethod.POST, "/api/updates").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers(HttpMethod.PUT, "/api/updates/{id}").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers(HttpMethod.DELETE, "/api/updates/{id}").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers("/api/users").hasAuthority("ROLE_FUNDRAISER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/me/stripe-id").hasAuthority("ROLE_FUNDRAISER") // API 21
                        .requestMatchers(HttpMethod.GET, "/api/updates/fundraiser/me").hasAuthority("ROLE_FUNDRAISER") // API 23
                        // Donor endpoints
                        .requestMatchers(HttpMethod.DELETE, "/api/updates/ratings/{ratingId}").hasAuthority("ROLE_DONOR")
                        .requestMatchers(HttpMethod.POST, "/api/donations/*/verify").hasAuthority("ROLE_DONOR") // Fix for API 18
                        .requestMatchers("/api/updates/volunteer/me/ratings").hasAuthority("ROLE_DONOR")
                        .requestMatchers(HttpMethod.PUT,"/api/users/me/volunteer").hasAuthority("ROLE_DONOR")
                        // Proof endpoints
                        .requestMatchers(HttpMethod.POST, "/api/proofs/charity/{charityId}/upload").hasAuthority("ROLE_FUNDRAISER") // Fix for API 19
                         // Default
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(
                authenticationProvider(userDetailsService(), passwordEncoder())
            )
            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            )
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
