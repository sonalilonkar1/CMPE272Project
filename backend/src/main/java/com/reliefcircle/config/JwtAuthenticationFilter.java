package com.reliefcircle.config;

import com.reliefcircle.service.JwtService;
import com.reliefcircle.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // ⛔ Skip filter for public endpoints
        final String requestPath = request.getServletPath();
        final String queryString = request.getQueryString();
        
        // Public endpoints without JWT required
        if (requestPath.startsWith("/api/auth/") || 
            requestPath.startsWith("/api/authenticate") || 
            (requestPath.equals("/api/charities") && request.getMethod().equals("GET") && queryString == null)) {
            log.debug("Skipping JWT filter for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Special case: If accessing /api/charities with query parameters but no auth header,
        // let the controller handle the unauthorized response for better error messages
        if (requestPath.equals("/api/charities") && request.getMethod().equals("GET") && queryString != null &&
            (authHeader == null || !authHeader.startsWith("Bearer "))) {
            log.debug("Request to /api/charities with parameters requires authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // For all other endpoints, check for valid Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found for path: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
            log.debug("JWT token parsed successfully, username: {}", userEmail);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT detected: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"JWT token has expired\"}");
            response.setContentType("application/json");
            return;
        } catch (JwtException e) {
            log.warn("Invalid JWT detected for path {}: {}", requestPath, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid JWT token: " + e.getMessage() + "\"}");
            response.setContentType("application/json");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                if (jwtService.isTokenValid(jwt, user)) {
                    log.debug("JWT token is valid for user: {}", user.getEmail());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token validation failed for user: {}", user.getEmail());
                }
            } else {
                log.warn("UserDetails is not an instance of User: {}", userDetails.getClass().getName());
            }
        }
        filterChain.doFilter(request, response);
    }

}