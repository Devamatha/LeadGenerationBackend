package com.lead.generation.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.lead.generation.exceptionhandling.CustomAccessDeniedHandler;
import com.lead.generation.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.lead.generation.filter.AuthoritiesLoggingAfterFilter;
import com.lead.generation.filter.AuthoritiesLoggingAtFilter;
import com.lead.generation.filter.JWTTokenGeneratorFilter;
import com.lead.generation.filter.JWTTokenValidatorFilter;
import com.lead.generation.filter.RequestValidationBeforeFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration

public class ProjectSecurityConfig {

	private static final String[] AUTH_WHITE_LIST = { "/v3/api-docs/**", "/swagger-ui/**", "/v2/api-docs/**",
			"/swagger-resources/**", "/webjars/**" };

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
		http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOriginPatterns(Collections.singletonList("*"));
						config.setAllowedMethods(Collections.singletonList("*"));
						config.setAllowCredentials(true);
						config.setAllowedHeaders(Collections.singletonList("*"));
						config.setExposedHeaders(Arrays.asList("Authorization"));
						config.setMaxAge(3600L);
						return config;
					}
				})).csrf(csrf -> csrf.disable())
				.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
				.addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
				.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
				.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/api/clients/changepassword/{Id}", "/api/lead/getAll-leads",
								"/api/lead/by-leadType/{client_id}", "/api/lead/{client_id}",
								"/api/lead/date/{client_id}", "/api/lead/getById/{Id}", "/api/lead/update/{id}",
								"/api/project/getAll")
						.authenticated().requestMatchers("/api/lead/saveLead/**", "/api/lead/count-per-day/{clientId}")
						.hasRole("EMPLOYEE").

						requestMatchers("/api/clients/saveEmployee/{adminId}", "/api/clients/employees/{adminId}",
								"/api/clients/update/{id}", "/api/project/save", "/api/project/update/{id}",
								"/api/project/delete/{Id}", "/api/project/getById/{Id}", "/api/project/getAllprojects",
								"/api/lead/getleadById/{clientId}","/api/clients/delete/{Id}","/api/clients/InActive-employees/{adminId}")
						.hasRole("ADMIN").requestMatchers("/api/clients/login", "/api/clients/saveAdmin", "/actuator")
						.permitAll().requestMatchers(AUTH_WHITE_LIST).permitAll()

				);

		http.formLogin(withDefaults());
		http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
		http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CompromisedPasswordChecker compromisedPasswordChecker() {
		return new HaveIBeenPwnedRestApiPasswordChecker();
	}

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		UserNamePwdAuthenticationProvider authenticationProvider = new UserNamePwdAuthenticationProvider(
				userDetailsService, passwordEncoder);
		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);
		return providerManager;
	}

}