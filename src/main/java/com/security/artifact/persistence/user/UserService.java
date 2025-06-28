package com.security.artifact.persistence.user;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.artifact.exceptions.NewUserWithDifferentPasswordException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

	public UserResponse getUserById(Integer userId) {
	    Optional<User> user = userRepository.findById(userId);
	    return user.map(value ->
	            UserResponse.builder()
	                    .firstName(value.getFirstName())
	                    .lastName(value.getLastName())
	                    .secondLastName(value.getSecondLastName())
	                    .email(value.getEmail())
	                    .userHeight(value.getUserHeight())
	                    .userWeight(value.getUserWeight())
	                    .registrationDate(value.getRegistrationDate())
	                    .build())
	            .orElse(null);
	}
    
    public Optional<User> getUserByIdCheck(Integer userId) {
        return userRepository.findById(userId);
    }

    public UserResponse updateUser(Integer userId, User updatedUser) {
        Optional<User> existingUserOptional = getUserByIdCheck(updatedUser.getId());

	    if(passwordEncoder.matches(updatedUser.getPassword(), existingUserOptional.get().getPassword())) {
	    	
	        if (existingUserOptional.isPresent() && 
	                (existingUserOptional.get().getId() == updatedUser.getId() || userId == 1)) {
	
	            User existingUser = existingUserOptional.get();
	
	            updatedUser.setPassword(existingUser.getPassword());
	
	            updatedUser.setTokens(existingUser.getTokens());
	            /*updatedUser.setRoutines(existingUser.getRoutines());
	            updatedUser.setWorkouts(existingUser.getWorkouts());
	            updatedUser.setFavouriteExercises(existingUser.getFavouriteExercises());
	
	            existingUser.getExercises().clear();
	            existingUser.getExercises().addAll(updatedUser.getExercises());
	            updatedUser.setExercises(existingUser.getExercises());*/
	
	            User savedUser = userRepository.save(updatedUser);
	
	            UserResponse userResponse = UserResponse.builder()
	                    .id(savedUser.getId())
	                    .firstName(savedUser.getFirstName())
	                    .lastName(savedUser.getLastName())
	                    .email(savedUser.getEmail())
	                    .registrationDate(savedUser.getRegistrationDate())
	                    .userWeight(savedUser.getUserWeight())
	                    .userHeight(savedUser.getUserHeight())
	                    .build();
	
	            return userResponse;
	        } else {
	            return null;
	        }
	    }else {
			throw new NewUserWithDifferentPasswordException();
	    }
    }


    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
    
    public List<UserResponse> findAll(String userRole) {
    	if(userRole.equals("ADMIN")) {
    		List<User> userList = userRepository.findAll();
            List<UserResponse> userResponses = userList.stream()
                    .map(user -> UserResponse.builder()
                    		.id(user.getId())
                            .firstName(user.getFirstName())
                            .email(user.getEmail())
                            .lastName(user.getLastName())
                            .secondLastName(user.getSecondLastName())
                            .registrationDate(user.getRegistrationDate())
                            .userHeight(user.getUserHeight())
                            .userWeight(user.getUserWeight())
                            .role(user.getRole().toString())
                            .build())
                    .sorted(Comparator.comparing(UserResponse::getId))
                    .collect(Collectors.toList());
    	return userResponses;
    } else {
    	return null;
    }
    	
    }
}

