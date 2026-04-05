package com.grocery.user.controller;

import com.grocery.auth.User;
import com.grocery.auth.UserRepository;
import com.grocery.common.ApiResponse;
import com.grocery.exception.ResourceNotFoundException;
import com.grocery.user.entity.Address;
import com.grocery.user.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getUserAddresses(Authentication authentication){
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found !"));
        List<Address> add = addressRepository.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, add));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Address>> saveAddress(
            Authentication authentication,
            @RequestBody Address address){
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        address.setUser(user); // Mapping address to current user
        Address saved = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success("Address added successfully!", saved));
    }

    @PatchMapping("/{addressId}/set-default")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @PathVariable Long addressId, Authentication authentication){
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        addresses.forEach(a -> a.setDefault(false));
        addressRepository.saveAll(addresses);

        Address newDefault = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found!"));
        newDefault.setDefault(true);
        addressRepository.save(newDefault);
        return ResponseEntity.ok(ApiResponse.success("Default address updated", null));
    }
}
