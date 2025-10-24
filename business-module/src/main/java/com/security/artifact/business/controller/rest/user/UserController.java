package com.security.artifact.business.controller.rest.user;

import com.security.artifact.business.service.user.UserService;
import com.security.artifact.data.dto.user.UserResponse;
import com.security.artifact.data.entity.user.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/all/admin/{userRole}")
    public ResponseEntity<List<UserResponse>> getAllUsers(@PathVariable String userRole) {
    	List<UserResponse> userList = userService.findAll(userRole);
    	
    	if(userList!=null) {
            return new ResponseEntity<>(userList, HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    	}
        
    }
    
    @PutMapping("/update/admin/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer userId, @RequestBody User updatedUser) {
    	UserResponse savedUser = userService.updateUser(userId, updatedUser);

        if (savedUser!=null) {
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.getUserById(userId);
        
        if(user!=null) {
        	return new ResponseEntity<>(user, HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        Optional<User> user = userService.getUserByIdCheck(userId);

        if (user.isPresent()) {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

