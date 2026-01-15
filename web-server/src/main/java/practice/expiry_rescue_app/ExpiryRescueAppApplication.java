package practice.expiry_rescue_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ExpiryRescueAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpiryRescueAppApplication.class, args);
	}

}
