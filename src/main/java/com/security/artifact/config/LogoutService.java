package com.security.artifact.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.security.artifact.token.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String jwt = null;

    if (request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          jwt = cookie.getValue();
          break;
        }
      }
    }

    if (jwt == null) {
      return;
    }

    var storedToken = tokenRepository.findByToken(jwt).orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
    }
  }
}
