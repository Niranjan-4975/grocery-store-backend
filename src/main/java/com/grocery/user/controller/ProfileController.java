package com.grocery.user.controller;

import com.grocery.common.ApiResponse;
import com.grocery.user.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.user.dto.UpdateUserRequest;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ProfileController {
	
	@Autowired
	private UserService userService;

    //1. Get Profile
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<User>> getMyProfile(Authentication authentication){
		User user = userService.getByEmail(authentication.getName());
        Address defaultAdd = userService.getDefaultAddress(user.getId());
        if (defaultAdd != null){
            user.setAddresses(List.of(defaultAdd));
        }else {
            user.setAddresses(Collections.emptyList());
        }
		return ResponseEntity.ok(ApiResponse.success(null,user));
	}

    //2. Update Profile
	@PutMapping("/update-profile")
	public ResponseEntity<ApiResponse<Void>> updateOwnProfile(
			@RequestBody UpdateUserRequest req,
            Authentication authentication){
		User current = userService.getByEmail(authentication.getName());
		userService.updateUser(current.getId(), req, current);
		return ResponseEntity.ok(ApiResponse.success("Profile updated successfully",null));
	}

}
