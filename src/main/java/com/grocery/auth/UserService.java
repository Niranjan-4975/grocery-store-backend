package com.grocery.auth;

import java.util.HashSet;
import java.util.Set;

import com.grocery.exception.ResourceNotFoundException;
import com.grocery.exception.UnauthorizedAccessException;
import com.grocery.exception.UserAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.grocery.config.SecurityConfig;
import com.grocery.user.dto.AdminCreateRequest;
import com.grocery.user.dto.UpdateUserRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // create user by User self Registration
    public String register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '"+ request.getEmail()+ "' is already in use");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_CUSTOMER); //default role
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile());
        user.setAddressLine(request.getAddressLine());
        user.setCity(request.getCity());
        user.setPinCode(request.getPinCode());
        user.setRoles(roles);
        userRepository.save(user);
        return "";
    }

    // create User by Admin
    public User createUserAsAdmin(AdminCreateRequest request, User currentAdmin) {
        //Only Admin Allowed
        if (!currentAdmin.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("Permission denied: Only Admins can create users");
        }
        //Check email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }
        System.out.println("Admin Request :"+request);
        // Hash Password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        //Convert Roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String roleStr : request.getRoles()) {
                try {
                    roles.add(Role.valueOf(roleStr));
                } catch (Exception e) {
                    throw new RuntimeException("Invalid role: " + roleStr);
                }
            }
        } else {
            roles.add(Role.ROLE_CUSTOMER);
        }
        //Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile());
        user.setAddressLine(request.getAddressLine());
        user.setCity(request.getCity());
        user.setPinCode(request.getPinCode());
        user.setRoles(roles);
        System.out.println("User after setting values: "+ user);
        return userRepository.save(user);
    }

    // Get User by I'd for Admin
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // Get Users for Admin
    public Page<User> getUsers(String search, String city, Pageable pageable) {
        //default values
        if (city == null) city = "";
        if (search == null) search = "";
        return userRepository.searchUsers(city, search, pageable);
    }

    // check password common
    public boolean checkPassword(String password, String passwordHash) {
        return passwordEncoder.matches(password, passwordHash);
    }

    // Update for Admin and User both
    public User updateUser(Long id, UpdateUserRequest request, User current) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isAdmin = current.getRoles().contains(Role.ROLE_ADMIN);
        //User Allowed filed update
        // 3. Permission Check: If not admin, ensure they are updating their own ID
        if (!isAdmin && !current.getId().equals(id)) {
            throw new UnauthorizedAccessException("You are not allowed to update this profile");
        }
        // --- 4. COMMON UPDATES (Allowed for Everyone) ---
        // This logic runs for both Admin and Customer
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        if (request.getAddressLine() != null) user.setAddressLine(request.getAddressLine());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getPinCode() != null) user.setPinCode(request.getPinCode());
        // --- 5. RESTRICTED UPDATES (Admin Only) ---
        if (isAdmin) {
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getAccountStatus() != null) user.setAccountStatus(request.getAccountStatus());
            // Update Roles safely
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                Set<Role> updatedRoles = new HashSet<>();
                for (String r : request.getRoles()) {
                    try {
                        updatedRoles.add(Role.valueOf(r));
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid roles
                    }
                }
                user.setRoles(updatedRoles);
            }
        }
        return userRepository.save(user);
    }


    // Delete user by id for Admin
    public void deleteUser(Long id, User currentAdmin) {
        if (!currentAdmin.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("Only Admin can delete user");
        }
        if (currentAdmin.getId().equals(id)) {
            throw new RuntimeException("Admin cannot delete their own account");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public void resetPasswordForUser(Long userId, String newPassword) {
        // 1. Find the user to be reset
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        // 2. Hash the new temporary password
        String newHashedPassword = passwordEncoder.encode(newPassword);
        // 3. Update the user's password field
        user.setPasswordHash(newHashedPassword);
        // 4. Save the updated user back to the database
        userRepository.save(user);
        // NOTE: In a production app, you would also log this event and notify the user
        // about their new password, later will implement when user try new password then system ask
        // to set new password
    }
}
