package com.escaperoom.escaperoom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IUserRepository;

@SpringBootApplication
public class EscaperoomApplication implements CommandLineRunner{
	@Autowired
	IUserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(EscaperoomApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// Création d'un admin
		User adminAccount = userRepository.findByRole(Role.ROLE_ADMIN);
		
		if(adminAccount == null) {
			
			User user = new User();
			
			user.setEmail("admin@mail.com");
			user.setPrenom("admin");
			user.setUsername("admin");
			user.setNom("admin@mail.com");
			user.setRole(Role.ROLE_ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			
			userRepository.save(user);
		}
		
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    // Déclare un bean pour encoder les mots de passe avec BCrypt (sécurisé et standard pour le hash)
	    return new BCryptPasswordEncoder();
	}

}
