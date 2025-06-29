package com.security.artifact.auth;

import com.security.artifact.config.JwtService;
import com.security.artifact.persistence.user.Role;
import com.security.artifact.persistence.user.User;
import com.security.artifact.persistence.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler  implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Crear REDACTED_PASSWORD nuevo asociado a OAuth2
            user = User.builder()
                    .firstName(name)
                    .lastName("")
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .provider(provider)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);

        // Retornar el JWT al frontend
        response.sendRedirect("http://localhost:4200/oauth2-redirect?token=" + token);
    }
}
