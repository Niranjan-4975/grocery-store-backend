package com.grocery.auth;

import com.grocery.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.grocery.common.JwtUtility;
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
	public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request){
		User user = userService.getByEmail(request.getEmail());
        //2. Validate Password
		if(!userService.checkPassword(request.getPassword(),user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid Credentials, pls check your details");
		}
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new DisabledException("Account is currently "+
                    user.getAccountStatus().name()+" .Contact support.");
        }
		String token = jwtUtility.generateToken(user);
        String role = user.getRoles().toString();
        AuthResponse authData = new AuthResponse(token,role,user.getEmail(),user.getFullName());
		return ResponseEntity.ok(ApiResponse.success("Login successful! Welcome back.", authData));
	}
	

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody RegistrationRequest request){
		String result = userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully! Please login now.", result));
	}

    @GetMapping("/check")
    public ResponseEntity<?> checkTokenValidity(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token is invalid or expired. Please login again."));
        }
        Map<String, Object> data = Map.of(
                "email", authentication.getName(),
                "roles", authentication.getAuthorities().stream().map(Object::toString).toList()
        );
        return ResponseEntity.ok(ApiResponse.success(null, data));
    }
}
