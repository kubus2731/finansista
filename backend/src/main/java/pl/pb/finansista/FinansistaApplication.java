package pl.pb.finansista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FinansistaApplication {

  static void main(String[] args) {
    SpringApplication.run(FinansistaApplication.class, args);
  }
}
