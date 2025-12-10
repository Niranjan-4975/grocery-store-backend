package com.grocery.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.grocery.common.JwtUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@CrossOrigin
public class AuthController {

    private final JwtUtility jwtUtility;
	private final UserService userService;
	
	public AuthController(UserService userService, JwtUtility jwtUtility) {
		this.userService = userService;
		this.jwtUtility = jwtUtility;
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request){
        System.out.println("Contoroller");
        // 1. Find User
		User user = userService.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("User not found"));
        //2. Validate Password
		if(!userService.checkPassword(request.getPassword(),user.getPasswordHash())) {
            // Throw exception here for consistency:
            throw new BadCredentialsException("Invalid Credentials, pls check your details");
		}
        //3. The Critical Check
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            // Throw an exception that Spring Security can handle
            // Spring Security typically maps AuthenticationException to a 401/403 response.
            throw new DisabledException("Account is currently "+
                    user.getAccountStatus().name()+" .Contact support.");
        }
		String token = jwtUtility.generateToken(user);
        String role = user.getRoles().toString();
		return ResponseEntity.ok(new AuthResponse(token,role,user.getEmail(),user.getFullName()));
	}
	

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request){
		userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully with ROLE_CUSTOMER");
	}

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkTokenValidity(Authentication authentication){
        // When this method is executed, Spring Security guarantees:
        // 1. The token was valid (signature check passed).
        // 2. The token was not expired (expiry check passed).
        // 3. The user principal (Authentication object) is available.

        // We return a simple confirmation and the user's principal name (email) and roles.
        // NOTE: The roles are typically accessible via authentication.getAuthorities()
        //1.Extract roles and convert to list
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Map<String, Object> response = new HashMap<>();
        response.put("email", authentication.getName());
        response.put("roles", roles);
        //Return the map
        return ResponseEntity.ok(response);
    }
}
