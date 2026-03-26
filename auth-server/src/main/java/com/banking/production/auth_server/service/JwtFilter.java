package com.banking.production.auth_server.service;

// Servlet API for request/response handling
import jakarta.servlet.*;
import jakarta.servlet.http.*;
// Spring Security authentication classes
import org.springframework.security.authentication.*;
// Spring Security context for storing authentication info
import org.springframework.security.core.context.SecurityContextHolder;
// Spring component annotation
import org.springframework.stereotype.Component;
// Base class for filters executed once per request
import org.springframework.web.filter.OncePerRequestFilter;

// For I/O operations
import java.io.IOException;

// For handling multiple header values
import java.util.Enumeration;

// Spring component annotation to register this as a filter bean
@Component
// JWT filter to validate tokens on each request before reaching endpoints
public class JwtFilter extends OncePerRequestFilter {

    // Utility for JWT token operations
    private final JwtUtil jwtUtil;
    // Service for loading user details from database
    private final CustomUserDetailsService userDetailsService;

    // Constructor with dependencies injected
    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Override filter method called for every request
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Log incoming request details for debugging
        // Extract HTTP method (GET, POST, etc.)
        String method = request.getMethod();
        // Extract request URI path
        String uri = request.getRequestURI();
        // Extract client IP address
        String remote = request.getRemoteAddr();
        System.out.println("JwtFilter: incoming request " + method + " " + uri + " from=" + remote);

        // Print all Authorization header values (helps detect proxies or duplicates)
        // Get all Authorization headers in case multiple are present
        Enumeration<String> authHeaders = request.getHeaders("Authorization");
        if (authHeaders != null && authHeaders.hasMoreElements()) {
            // Iterate through all Authorization headers
            int i = 0;
            while (authHeaders.hasMoreElements()) {
                String val = authHeaders.nextElement();
                System.out.println("JwtFilter: Authorization header[#" + (i++) + "] = ['" + val + "']");
            }
        } else {
            System.out.println("JwtFilter: No Authorization header present (getHeaders returned none)");
        }

        // Get the single Authorization header value
        String header = request.getHeader("Authorization");

        // Process the Authorization header if present
        if (header != null) {
            // Extract token from header with flexible parsing
            String token = null;
            // Trim whitespace from header
            String headerTrim = header.trim();
            // Check for "Bearer " prefix (standard format) - case insensitive
            if (headerTrim.toLowerCase().startsWith("bearer ")) {
                // Extract token after "Bearer " prefix (7 characters)
                token = headerTrim.substring(7).trim();
            } else if (headerTrim.startsWith("Bearer ")) {
                // Fallback for capital B
                token = headerTrim.substring(7).trim();
            } else {
                // If no "Bearer " prefix, assume the entire header is the token
                token = headerTrim;
            }

            System.out.println("JwtFilter: Received Authorization header. original=['" + header + "'] extractedToken=['" + token + "']");

            try {
                // Validate and process the token
                System.out.println("JwtFilter: validating token...");
                // Check if token exists and is valid
                if (token != null && jwtUtil.isValid(token)) {

                    // Extract email (subject) from the token
                    String email = jwtUtil.extractEmail(token);
                    System.out.println("JwtFilter: token valid, email='" + email + "'");

                    // Load user details from database using the extracted email
                    var userDetails = userDetailsService.loadUserByUsername(email);

                    // Create authentication token with user details and authorities
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    // Set the authentication in the Spring Security context
                    // This makes the user authenticated for the current request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Log if token is invalid or null
                    System.out.println("JwtFilter: token invalid or null");
                }
            } catch (Exception e) {
                // Log any exceptions during token validation but don't block the request
                System.out.println("JwtFilter: exception while validating token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Log if no Authorization header is present
            System.out.println("JwtFilter: No Authorization header present");
        }

        // Continue to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }
}
