package com.grocery.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.grocery.auth.User;
import com.grocery.auth.UserRepository;
import com.grocery.common.JwtUtility;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtUtility jwtUtility;
	@Autowired
	private UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtility jwtUtility, UserRepository userRepository) {
        this.jwtUtility = jwtUtility;
        this.userRepository = userRepository;
    }

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException{

        String method = request.getMethod();
        // 1. Wrap the request only if it's POST/PUT and hits the protected admin/products path
        if (("POST".equals(method) || "PUT".equals(method)) &&
                request.getRequestURI().contains("/api/admin/products")) {
            // Wrap the original request
            request = new CachedBodyHttpServletRequest(request);
        }

        //1. Get Custom header
		final String jwtToken = request.getHeader("user-payload");
        System.out.println("Reqest heder: "+ jwtToken);
        String email = null;
        //2. Check If token exists and Not blank
        if(jwtToken != null && !jwtToken.isBlank()){
            try{
                email = jwtUtility.extractEmail(jwtToken);
                System.out.println("Extract email: "+ email);
            } catch (ExpiredJwtException e){
                System.out.println(e.getMessage());
                System.out.println("Token Parsing Failed: " + e.getMessage()); // Debug Error
            }
        }
		//3. Validate and authenticate user
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userRepository.findByEmail(email).orElse(null);
			if (user != null && jwtUtility.validateToken(jwtToken, user.getEmail())) {
				List<String> roles = jwtUtility.extractRoles(jwtToken);
                System.out.println("Extracted role: "+ roles);
//				List<GrantedAuthority> authorities = roles.stream()
//						.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//                System.out.println("authorities :"+ authorities);
				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		//Step 3 Continue the chain
		filterChain.doFilter(request, response);
	}
}
