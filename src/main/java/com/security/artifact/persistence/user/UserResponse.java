package com.security.artifact.persistence.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	
	private Integer id;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String email;
    private Date registrationDate; 
	private int userWeight;
	private int userHeight;
	private String role;
	private String password;

}
