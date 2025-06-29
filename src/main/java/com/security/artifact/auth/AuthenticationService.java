package com.security.artifact.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.security.artifact.config.JwtService;
import com.security.artifact.exceptions.NewUserWithDifferentPasswordException;
import com.security.artifact.persistence.user.Role;
import com.security.artifact.persistence.user.User;
import com.security.artifact.persistence.user.UserRepository;
import com.security.artifact.token.Token;
import com.security.artifact.token.TokenRepository;
import com.security.artifact.token.TokenType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;

	private final TokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;
	
	private static final Logger log = LogManager.getLogger(AuthenticationService.class);

	public AuthenticationResponse register(RegisterRequest request) throws NewUserWithDifferentPasswordException {
		
		if(request.getPassword().contentEquals(request.getPassword2())) {
			var user = User.builder()
					.firstName(request.getFirstname())
					.lastName(request.getLastname())
					.secondLastName(request.getSecondLastName())
					.provider("LOCAL")
					.email(request.getEmail())
					.userWeight(request.getUserWeight())
					.userHeight(request.getUserHeight())
					.password(passwordEncoder.encode(request.getPassword()))
					.role(request.getRole() != null ? request.getRole() : Role.USER)
					.build();
			try {
				var savedUser = userRepository.save(user);
				var jwtToken = jwtService.generateToken(user);
				saveUserToken(savedUser, jwtToken);
				log.info("AuthenticationService() - User registered");

				
				return AuthenticationResponse.builder()
						.token(jwtToken)
						.userId(user.getId())
						.role(user.getRole())
						.build();				
			} catch(DataIntegrityViolationException ex) {
				log.error("AuthenticationService() - User cannot be registered beacuse the email is in use");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este email ya está en uso");		
			}
			
		} else {
			log.error("AuthenticationService() - User cannot be registered beacuse the password does not match");
			throw new NewUserWithDifferentPasswordException();
		}
		
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user = userRepository
				.findByEmail(request.getEmail())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.userId(user.getId())
				.role(user.getRole())
				.build();
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty()) {
			return;
		}
		validUserTokens.forEach(t -> {
			t.setExpired(true);
			t.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	private void saveUserToken(User savedUser, String jwtToken) {
		var token = Token.builder()
				.user(savedUser)
				.token(jwtToken)
				.tokenType(TokenType.BEARER)
				.revoked(false)
				.expired(false).build();
		tokenRepository.save(token);
	}
	
	
	

}
