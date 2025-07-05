package com.security.artifact.config;

import java.io.IOException;
import java.util.Set;

import com.security.artifact.data.repository.token.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final TokenRepository tokenRepository;

	private static final Set<String> PUBLIC_PATHS = Set.of(
			"/api/auth/register",
			"/api/auth/authenticate",
			"/oauth2/authorization"
	);

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

		String path = request.getServletPath();
		logger.info("(JwtAuthenticationFilter.doFilterInternal) - Recieved path: " + path);

		boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
		if (isPublic) {
			filterChain.doFilter(request, response);
			return;
		}

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
			logger.info("(JwtAuthenticationFilter.doFilterInternal) - Token is null");
			filterChain.doFilter(request, response);
			return;
		}

		final String userEmail = jwtService.extractUserName(jwt);
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
			var isTokenValid = tokenRepository.findByToken(jwt)
					.map(t -> !t.isExpired() && !t.isRevoked())
					.orElse(false);
			logger.debug("(JwtAuthenticationFilter.doFilterInternal) - Token is valid for signature: " + jwtService.isTokenValid(jwt, userDetails));

			if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
				);
				authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
