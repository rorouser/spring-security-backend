package com.security.artifact.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;

	private final TokenRepository tokenRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;
	
	private static final Logger log = LogManager.getLogger(AuthenticationService.class);

	public ResponseEntity<?> register(RegisterRequest request) {

		if (!request.getPassword().contentEquals(request.getPassword2())) {
			log.error("AuthenticationService() - User cannot be registered because the passwords do not match");
			return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
		}

		var user = User.builder()
				.firstName(request.getFirstname())
				.lastName(request.getLastname())
				.secondLastName(request.getSecondLastName())
				.provider("LOCAL")
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole() != null ? request.getRole() : Role.USER)
				.build();

		try {
			var savedUser = userRepository.save(user);
			var jwtToken = jwtService.generateToken(user);
			saveUserToken(savedUser, jwtToken);
			log.info("AuthenticationService() - User registered");

			ResponseCookie cookie = createJwtCookie(jwtToken);

			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(Map.of(
							"userId", user.getId(),
							"role", user.getRole()
					));
		} catch (DataIntegrityViolationException ex) {
			log.error("AuthenticationService() - User cannot be registered because the email is in use");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Este email ya está en uso");
		}
	}

	public ResponseEntity<?> authenticate(AuthenticationRequest request) {

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);

		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);

		ResponseCookie cookie = createJwtCookie(jwtToken);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(Map.of(
						"userId", user.getId(),
						"role", user.getRole()
				));
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

	private ResponseCookie createJwtCookie(String jwtToken) {
		return ResponseCookie.from("token", jwtToken)
				.httpOnly(true)
				.secure(true) // En producción HTTPS
				.path("/")
				.maxAge(24 * 60 * 60) // 1 día
				.sameSite("Strict") // o "Lax" según tu necesidad
				.build();
	}
	

}
