package pl.pb.finansista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FinansistaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinansistaApplication.class, args);
	}

}
