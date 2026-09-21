package com.icici.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icici.dto.LoginRequest;
import com.icici.dto.LoginResponse;
import com.icici.dto.RegisterRequest;
import com.icici.entity.AppUser;
import com.icici.repo.AppUserRepo;
import com.icici.security.BankingUserDetailsService;
import com.icici.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
	AuthenticationManager authManager;
	BankingUserDetailsService userDetailsService;
	JwtUtil jwtUtil;
	AppUserRepo appUserRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	public AuthController(AuthenticationManager authManager, BankingUserDetailsService userDetailsService,
			JwtUtil jwtUtil, AppUserRepo appUserRepo) {
		super();
		this.authManager = authManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.appUserRepo = appUserRepo;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req){
		authManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
				);
		UserDetails ud = userDetailsService.loadUserByUsername(req.getUsername());
		String token = jwtUtil.generateToken(ud);
		String role = ud.getAuthorities().iterator().next().getAuthority();
		return ResponseEntity.ok(new LoginResponse(token, 600, role));
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
	    if (appUserRepo.findByUsername(req.getUsername()).isPresent())
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
	    if (req.getRole() == null || !req.getRole().startsWith("ROLE_"))
	        return ResponseEntity.badRequest().body("Role must start with ROLE_");
	    appUserRepo.save(new AppUser(null, req.getUsername(),
	        passwordEncoder.encode(req.getPassword()), req.getRole()));
	    return ResponseEntity.status(HttpStatus.CREATED).body("User registered: " + req.getUsername());
	}
}
