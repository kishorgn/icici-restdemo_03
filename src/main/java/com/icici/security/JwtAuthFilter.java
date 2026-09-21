package com.icici.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	JwtUtil jwtUtil;
	BankingUserDetailsService userDetailsService;
	
	@Autowired
	public JwtAuthFilter(JwtUtil jwtUtil, BankingUserDetailsService userDetailsService) {
		super();
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = req.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}
		String token = authHeader.substring(7);
		try {
			String username = jwtUtil.extractUsername(token);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
				UserDetails ud = userDetailsService.loadUserByUsername(username);
				if( jwtUtil.isTokenValid(token, ud) ) {
					UsernamePasswordAuthenticationToken authToken 
								= new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		chain.doFilter(req, res);
	}

}
