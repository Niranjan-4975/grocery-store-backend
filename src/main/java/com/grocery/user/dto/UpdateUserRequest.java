package com.grocery.user.dto;

import java.util.Set;

import com.grocery.auth.AccountStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateUserRequest {

	private String fullName;
	private String email;
	private String mobile;
	private String addressLine;
	private String city;
	private String pinCode;
	private Set<String> roles;
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus = AccountStatus.ACTIVE;
}
