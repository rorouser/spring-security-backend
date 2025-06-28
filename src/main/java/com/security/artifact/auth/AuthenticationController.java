package com.security.artifact.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.security.artifact.exceptions.NewUserWithDifferentPasswordException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
	    try {
	        AuthenticationResponse response = authenticationService.register(request);
	        return ResponseEntity.ok(response);
	    } catch (NewUserWithDifferentPasswordException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Las contraseñas no coinciden");
	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode())
	        		.body(e.getReason());
	    }
	}


	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}
}
