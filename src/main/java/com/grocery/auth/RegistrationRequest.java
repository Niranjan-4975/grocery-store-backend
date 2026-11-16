package com.grocery.auth;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "Full name is required")
	private String fullName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
	private String email;
    @Size(min = 8, message = "Password must be at least 6 characters")
	private String password;
    @Pattern(regexp = "^\\d{10}$", message = "Mobile must be 10 digits")
	private String mobile;
    @NotBlank(message = "Address is required")
	private String addressLine;
    @NotBlank(message = "City is required")
	private String city;
    @NotBlank(message = "Pin code is required")
	private String pinCode;
	private Set<String> roles;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus = AccountStatus.ACTIVE;

	@JsonSetter("roles")
	public void setRoles(Object roles) {
		this.roles = new HashSet<>();
		if (roles instanceof String) {
			this.roles.add((String) roles);
		} else if (roles instanceof Iterable<?>) {
			for (Object o: (Iterable<?>) roles) {
				this.roles.add(String.valueOf(o));
			}
		}
	}
}
