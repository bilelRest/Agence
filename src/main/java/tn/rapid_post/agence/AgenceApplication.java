package tn.rapid_post.agence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.service.AppUserInterfaceImpl;

@SpringBootApplication
public class AgenceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AgenceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner start(AppUserInterfaceImpl appUserInterface) {
		return args -> {
			// exemple d'utilisation au démarrage

//appUserInterface.AddRoleToUser("bilel","ADMIN");
// appUserInterface.AddUser(...);
		};
	}
}
