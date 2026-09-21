package com.icici.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.icici.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	JwtAuthFilter jwtAuthFilter;
	UserDetailsService userDetailsService;
	
	@Autowired
	public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		super();
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
			.csrf( csrf -> csrf.disable() )
			.authorizeHttpRequests( auth -> auth
					.requestMatchers("/auth/login","/auth/register").permitAll()
					.requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/**").hasAnyRole("ADMIN", "TELLER")
					.requestMatchers(HttpMethod.PUT, "/**").hasAnyRole("ADMIN", "TELLER")
					.requestMatchers(HttpMethod.PATCH, "/**").hasAnyRole("ADMIN", "TELLER")
					.requestMatchers(HttpMethod.GET, "/**").authenticated()
					.anyRequest().denyAll()
			).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception{
		return cfg.getAuthenticationManager();
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider p = new DaoAuthenticationProvider(userDetailsService);
		p.setPasswordEncoder(passwordEncoder());
		return p;
	}
	
}
