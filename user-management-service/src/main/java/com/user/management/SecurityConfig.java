package com.user.management;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.user.management.enums.RoleType;
import com.user.management.security.JwtAuthenticationEntryPoint;
import com.user.management.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
		
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable().authorizeHttpRequests((authorize) -> 
		              authorize
//		              .requestMatchers("/api/**").permitAll()
		              .requestMatchers("/api/auth/**").permitAll()
//		              .requestMatchers(HttpMethod.GET,"/api/bank/**").permitAll()
		              
		              .requestMatchers("/api/bank/customers/**").hasAnyRole("BRANCH_MANAGER")
		              
		              .requestMatchers(HttpMethod.GET,"/api/bank/accounts/findBankAccount/**").hasAnyRole("CUSTOMER")
		              .requestMatchers("/api/bank/accounts/**").hasAnyRole("BRANCH_MANAGER")
		              
		              .requestMatchers("/api/bank/transactions/**").hasAnyRole("BRANCH_MANAGER","CUSTOMER")
		              
		              .anyRequest().authenticated())
		              .httpBasic(Customizer.withDefaults())
		           .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
		           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
		
	}

}
