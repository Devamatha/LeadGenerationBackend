package com.lead.generation.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lead.generation.constants.ApplicationConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String jwt = request.getHeader(ApplicationConstants.JWT_HEADER);
		if (null != jwt) {
			try {
				Environment env = getEnvironment();
				if (null != env) {
					String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
							ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
					SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
					if (null != secretKey) {
						Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
						String username = String.valueOf(claims.get("username"));
						String authorities = String.valueOf(claims.get("authorities"));
						Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
								AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}

			} catch (ExpiredJwtException ex) {
				sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JWT expired",
						ex.getMessage(), request.getServletPath());
				return;
			} catch (Exception exception) {
				throw new BadCredentialsException("Invalid Token received!");
			}
		}
		filterChain.doFilter(request, response);
	}

	private void sendErrorResponse(HttpServletResponse response, int status, String error, String message, String path)
			throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");

		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("timestamp", LocalDateTime.now().toString());
		errorDetails.put("status", status);
		errorDetails.put("error", error);
		errorDetails.put("message", message);
		errorDetails.put("path", path);

		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = mapper.writeValueAsString(errorDetails);
		response.getWriter().write(jsonResponse);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return request.getServletPath().equals("/user");
	}

}
