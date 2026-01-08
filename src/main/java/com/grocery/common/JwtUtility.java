package com.grocery.common;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.grocery.auth.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {

	
	private final String SECRET = "my-super-secret-key-my-super-secret-key";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 🕒 Expiry times (ms)
    private final long ADMIN_EXPIRY = 900000; // 15 Mins
    private final long CUSTOMER_EXPIRY = 604800000;

	public String generateToken(User user) {
        //1. Convert Set<Role> enums to List<String>
        List<String> roleNames = user.getRoles().stream()
                .map(Enum::name) // Correctly maps Role.ROLE_ADMIN to "ROLE_ADMIN"
                .toList();
        // ✅ Step 1: Check Role to set dynamic expiration
        long currentExpiry = roleNames.contains("ROLE_ADMIN") ? ADMIN_EXPIRY: CUSTOMER_EXPIRY;
		return Jwts.builder().setSubject(user.getEmail())
				.claim("roles", roleNames)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+ currentExpiry))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

    //2. Validate if token expired
    public boolean isTokenExpired(String token){
        try{
            return getClaims(token).getExpiration().before(new Date());
        }catch (Exception e){
            return true;
        }
    }

	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public String extractEmail(String token) {
		return getClaims(token).getSubject();
	}
	
	public List<String> extractRoles(String token){
		Object roles = getClaims(token).get("roles");
		if (roles instanceof List<?>) {
			return ((List<?>)roles).stream()
					.map(Object::toString)
					.toList();
		}
		return List.of();
	}
	
	public boolean validateToken(String token, String email) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			String tokenEmail = claims.getBody().getSubject();
			return tokenEmail.equals(email);
		}catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
	
	
}
