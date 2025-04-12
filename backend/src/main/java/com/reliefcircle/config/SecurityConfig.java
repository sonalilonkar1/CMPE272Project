package com.reliefcircle.config;

import com.reliefcircle.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2UserService<OidcUserRequest, OidcUser> customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // CSRF disabled for REST API
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers("/api/authenticate", "/api/users/register").permitAll()
                    .antMatchers("/api/charities/*/approve").hasAuthority("ROLE_ADMIN")
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .userInfoEndpoint(userInfo ->
                        userInfo.oidcUserService(customOAuth2UserService)
                    )
                    .defaultSuccessUrl("/api/donations", true)
                    .failureUrl("/api/authenticate?error=true")
            )
            .logout(logout ->
                logout
                    .logoutUrl("/logout") // Explicitly set to /logout
                    .logoutSuccessUrl("/api/authenticate")
                    .invalidateHttpSession(true) // Clear session
                    .deleteCookies("JSESSIONID") // Remove session cookie
                    .permitAll()
            );
    }
}