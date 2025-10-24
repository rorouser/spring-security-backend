package com.security.artifact.auth;


import com.security.artifact.data.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	
	private String token;
	
	private Integer userId;
	
	private Role role;

}
	