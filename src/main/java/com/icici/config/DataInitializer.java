package com.icici.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.icici.entity.AppUser;
import com.icici.repo.AppUserRepo;

@Component
public class DataInitializer implements CommandLineRunner {
	AppUserRepo appUserRepo;
	PasswordEncoder enc;
	
	public DataInitializer(AppUserRepo appUserRepo, PasswordEncoder enc) {
		super();
		this.appUserRepo = appUserRepo;
		this.enc = enc;
	}

	@Override
	public void run(String... args) throws Exception {
		if (appUserRepo.count() == 0) {
			appUserRepo.saveAll(List.of(
					new AppUser(null, "admin", enc.encode("admin123"), "ROLE_ADMIN"),
					new AppUser(null, "teller01", enc.encode("teller123"), "ROLE_TELLER"),
					new AppUser(null, "cust001", enc.encode("cust123"), "ROLE_CUSTOMER")
					));
		}
		
	}

}
