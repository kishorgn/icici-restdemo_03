package com.icici.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.icici.repo.AppUserRepo;

@Service
public class BankingUserDetailsService implements UserDetailsService {
	
	AppUserRepo appUserRepo;
	
	@Autowired
	public BankingUserDetailsService(AppUserRepo appUserRepo) {
		super();
		this.appUserRepo = appUserRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepo.findByUsername(username)
				.map( u -> new User(
						u.getUsername(),
						u.getPassword(),
						List.of(new SimpleGrantedAuthority(u.getRole()))
				))
				.orElseThrow(() -> new UsernameNotFoundException("User not found : "+username));
	}

}
