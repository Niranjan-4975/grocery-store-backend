package com.grocery.user.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.auth.Role;
import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.common.JwtUtility;
import com.grocery.user.dto.AdminCreateRequest;
import com.grocery.user.dto.UpdateUserRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private JwtUtility jwtUtility;

	// Get users for Admin
	@GetMapping("/getusers")
	public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String [] sort,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search,
            Authentication authentication){
	    // Convert sort param → Pageable
	    Sort.Direction direction = Sort.Direction.fromString(sort[1]);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
	    Page<User> users = userService.getUsers(search, city, pageable);
	    return ResponseEntity.ok(users);
	}
	
	// Get User by Id for Admin
	@GetMapping("/getusers/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id){
		User user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}
	
	// create user for Admin
	@PostMapping("/create-user")
	public ResponseEntity<?> createUser(
			@RequestBody AdminCreateRequest request,
			Authentication authentication){
		String email = authentication.getName();
		User currentAdmin = userService.getByEmail(email);
		userService.createUserAsAdmin(request, currentAdmin);
		return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
	}

	// update user partially for Admin
	@PatchMapping("/users/{id}")
	public ResponseEntity<?> updateUserAsAdminPatch(
			@PathVariable Long id,
			@RequestBody UpdateUserRequest req,
			Authentication authentication){
		String email = authentication.getName();
		User currentAdmin = userService.getByEmail(email);
		userService.updateUser(id, req, currentAdmin);
		return ResponseEntity.ok("User updated successfully");
	}

	// update user completly for Admin
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
	public ResponseEntity<?> deleteUser(
			@PathVariable Long id,
			Authentication authentication){
		String email = authentication.getName();
		User currentAdmin = userService.getByEmail(email);
		userService.deleteUser(id, currentAdmin);
		return ResponseEntity.ok("User deleted successfully");
	}

    // ✅ NEW ENDPOINT: Reset User Password
    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            Authentication authentication) {
        // NOTE: The role validation (hasAuthority('ROLE_ADMIN')) is handled by Spring Security,
        userService.resetPasswordForUser(id, newPassword);
        return ResponseEntity.ok("Password reset initiated for user " + id);
    }
}

