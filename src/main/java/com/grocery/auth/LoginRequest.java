package com.grocery.auth;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LoginRequest {

	private String email;
	private String password;
}
