package com.grocery.user.controller;

import com.grocery.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.user.dto.AdminCreateRequest;
import com.grocery.user.dto.UpdateUserRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
	
	@Autowired
	private UserService userService;

    // Get users for Admin
	@GetMapping("/getusers")
	public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String [] sort,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search){
	    // Convert sort param → Pageable
	    Sort.Direction direction = Sort.Direction.fromString(sort[1]);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
	    Page<User> users = userService.getUsers(search, city, pageable);
	    return ResponseEntity.ok(ApiResponse.success(null,users));
	}
	
	// Get User by I'd for Admin
	@GetMapping("/getusers/{id}")
	public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id){
		User user = userService.getUserById(id);
		return ResponseEntity.ok(ApiResponse.success(null,user));
	}
	
	// create user for Admin
	@PostMapping("/create-user")
	public ResponseEntity<ApiResponse<User>> createUserAsAdmin(
			@RequestBody AdminCreateRequest request,
			Authentication authentication){
		User currentAdmin = userService.getByEmail(authentication.getName());
		User userData = userService.createUserAsAdmin(request, currentAdmin);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully",userData));
	}

	// update user partially for Admin
	@PatchMapping("/users/{id}")
	public ResponseEntity<ApiResponse<Void>> updateUserAsAdminPatch(
			@PathVariable Long id,
			@RequestBody UpdateUserRequest req,
			Authentication authentication){
		String email = authentication.getName();
		User currentAdmin = userService.getByEmail(email);
		userService.updateUser(id, req, currentAdmin);
		return ResponseEntity.ok(ApiResponse.success("User updated successfully",null));
	}

	// update user completely for Admin
	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUserAsAdminPut(
			@PathVariable Long id,
			@RequestBody UpdateUserRequest req,
			Authentication authentication){
		String email = authentication.getName();
		User currentAdmin = userService.getByEmail(email);
		userService.updateUser(id, req, currentAdmin);
		return ResponseEntity.ok("User updated successfully");
	}
	
	// Delete user by id for Admin
	@DeleteMapping("/users/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(
			@PathVariable Long id,
			Authentication authentication){
		User currentAdmin = userService.getByEmail(authentication.getName());
		userService.deleteUser(id, currentAdmin);
		return ResponseEntity.ok(ApiResponse.success("User deleted successfully",null));
	}

    // ✅ NEW ENDPOINT: Reset User Password
    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            Authentication authentication) {
        userService.resetPasswordForUser(id, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful for user ID" + id,null));
    }
}

