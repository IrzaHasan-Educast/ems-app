package com.educast.ems.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
	
	    String path = request.getRequestURI();
	    if (path.startsWith("/api/v1/auth")) {
	        // Skip login/register endpoints
	        filterChain.doFilter(request, response);
	        return;
	    }
	
	    final String authHeader = request.getHeader("Authorization");
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        try {
	            String username = jwtUtil.extractClaims(token).getSubject();
	            String role = (String) jwtUtil.extractClaims(token).get("role");
	
	            UsernamePasswordAuthenticationToken authToken =
	                    new UsernamePasswordAuthenticationToken(
	                            username,
	                            null,
	                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
	                    );
	
	            SecurityContextHolder.getContext().setAuthentication(authToken);
	
	        } catch (Exception e) {
	            System.out.println("JWT validation failed: " + e.getMessage());
	        }
	    }
	
	    filterChain.doFilter(request, response);
	    System.out.println("Authenticated user: " + SecurityContextHolder.getContext().getAuthentication());

	}

}
