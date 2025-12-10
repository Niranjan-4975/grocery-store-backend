package com.grocery.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.user.dto.UpdateUserRequest;

@RestController
@RequestMapping("/api/users")
public class ProfileController {
	
	@Autowired
	private UserService userService;

    //1. Get Profile
	@GetMapping("/me")
	public ResponseEntity<User> getMyProfile(Authentication authentication){
		String email = authentication.getName();
		User user = userService.getByEmail(email);
		return ResponseEntity.ok(user);
	}

    //2. Update Profile
	@PutMapping("/update-profile")
	public ResponseEntity<?> updateOwnProfile(
			@RequestBody UpdateUserRequest req,
            Authentication authentication){
		
		String email = authentication.getName();
		User current = userService.getByEmail(email);
		userService.updateUser(current.getId(), req, current);
		return ResponseEntity.ok("Profile updated successfully");
	}

}
