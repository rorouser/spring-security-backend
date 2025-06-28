package com.security.artifact.auth;

import com.security.artifact.persistence.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	
	private String firstname;
	
	private String lastname;
	
	private String secondLastName;
	
	private String email;
	
	private int userHeight;
	
	private int userWeight;
	
	private Role role;
	
	private String password;
	
	private String password2;

}
