package com.security.artifact.config;

import com.security.artifact.auth.AuthenticationService;
import com.security.artifact.auth.RegisterRequest;
import com.security.artifact.business.service.user.UserService;
import com.security.artifact.data.entity.user.Role;
import com.security.artifact.data.entity.user.User;
import com.security.artifact.data.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Component
public class DatabaseInitializer {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationService service;
	
	@Autowired
	private UserService userService;
	
 	@Value("${admin.firstname}")
    private String adminFirstname;

    @Value("${admin.lastname}")
    private String adminLastname;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

	@PostConstruct
	@Transactional
	public void initializeExercises() {
		
		var admin = RegisterRequest.builder()
				.firstname(adminFirstname)
				.lastname(adminLastname)
				.email(adminEmail)
				.password(adminPassword)
				.password2(adminPassword)
				.role(Role.ADMIN)
				.build();
		
		var user0 = RegisterRequest.builder()
				.firstname("Iratxe")
				.lastname("Llaga")
				.email("user@user.com")
				.userWeight(50)
				.userHeight(160)
				.password("REDACTED_PASSWORD")
				.password2("REDACTED_PASSWORD")
				.role(Role.USER)
				.build();
		
		
		service.register(admin);
		User user = userRepository.findById(1).orElse(null); 

        if (user == null) {
            throw new IllegalStateException("User with ID 1 not found");
        }

		service.register(user0);

		
	    
	    for (int i = 2; i < 12; i++) {
            String email = "user" + i + "@user.com"; 
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .firstname("Manuel")
                    .lastname("Montoya")
                    .email(email)
                    .password("user")
                    .password2("user")
                    .role(Role.USER)
                    .build();

            service.register(registerRequest);
	    }
	    	

//         LocalDateTime initialDate = LocalDateTime.now();
//        
//		 for (int i = 2; i < 502; i++) {
//	            String email = "user" + i + "@user.com"; 
//	            RegisterRequest registerRequest = RegisterRequest.builder()
//	                    .firstname("Manuel")
//	                    .lastname("Montoya")
//	                    .email(email)
//	                    .password("user")
//	                    .password2("user")
//	                    .role(Role.USER)
//	                    .build();
//
//	            service.register(registerRequest); 
//	            
//	            Date currentDate = Date.from(initialDate.minusMonths(i).atZone(ZoneId.systemDefault()).toInstant());
//	            
//                User userToUpdate = getUserForPart(userService, i); // Obtener el REDACTED_PASSWORD específico
//                if (userToUpdate != null) {
//                    userToUpdate.setRegistrationDate(currentDate);
//                    userService.updateUser(userToUpdate.getId(), userToUpdate); // Actualizar el REDACTED_PASSWORD
//                }
//	            
//	        }
//		 }
//		 
//		 	
//
//		// Método para obtener un REDACTED_PASSWORD específico por su ID
//		private User getUserForPart(UserService userService, int userId) {
//		    UserResponse userResponse = userService.getUserById(userId);
//		    if (userResponse != null) {
//		        return User.builder()
//		                .id(userId)
//		                .firstName(userResponse.getFirstName())
//		                .lastName(userResponse.getLastName())
//		                .secondLastName(userResponse.getSecondLastName())
//		                .email(userResponse.getEmail())
//		                .userHeight(userResponse.getUserHeight())
//		                .userWeight(userResponse.getUserWeight())
//		                .registrationDate(userResponse.getRegistrationDate())
//		                .build();
//		    } else {
//		        return null; 
//		    }
		}


}
