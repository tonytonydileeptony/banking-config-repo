package com.banking.production.auth_server.service;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Enumeration;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Request context for debugging
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String remote = request.getRemoteAddr();
        System.out.println("JwtFilter: incoming request " + method + " " + uri + " from=" + remote);

        // Print all Authorization header values (helps detect proxies or duplicates)
        Enumeration<String> authHeaders = request.getHeaders("Authorization");
        if (authHeaders != null && authHeaders.hasMoreElements()) {
            int i = 0;
            while (authHeaders.hasMoreElements()) {
                String val = authHeaders.nextElement();
                System.out.println("JwtFilter: Authorization header[#" + (i++) + "] = ['" + val + "']");
            }
        } else {
            System.out.println("JwtFilter: No Authorization header present (getHeaders returned none)");
        }

        String header = request.getHeader("Authorization");

        if (header != null) {
            // make header handling case-insensitive and tolerant
            String token = null;
            String headerTrim = header.trim();
            if (headerTrim.toLowerCase().startsWith("bearer ")) {
                token = headerTrim.substring(7).trim();
            } else if (headerTrim.startsWith("Bearer ")) { // fallback
                token = headerTrim.substring(7).trim();
            } else {
                // maybe client sent only the token without "Bearer " prefix
                token = headerTrim;
            }

            System.out.println("JwtFilter: Received Authorization header. original=['" + header + "'] extractedToken=['" + token + "']");

            try {
                System.out.println("JwtFilter: validating token...");
                if (token != null && jwtUtil.isValid(token)) {

                    String email = jwtUtil.extractEmail(token);
                    System.out.println("JwtFilter: token valid, email='" + email + "'");

                    var userDetails = userDetailsService.loadUserByUsername(email);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("JwtFilter: token invalid or null");
                }
            } catch (Exception e) {
                // log for debugging - don't fail the request here
                System.out.println("JwtFilter: exception while validating token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("JwtFilter: No Authorization header present");
        }

        filterChain.doFilter(request, response);
    }
}
