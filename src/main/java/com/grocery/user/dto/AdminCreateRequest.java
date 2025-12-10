package com.grocery.user.dto;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.grocery.auth.AccountStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdminCreateRequest {
	
	private String fullName;
	private String email;
	private String password;
	private String mobile;
	private String addressLine;
	private String city;
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
