package pl.pb.finansista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class FinansistaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinansistaApplication.class, args);
	}

}
