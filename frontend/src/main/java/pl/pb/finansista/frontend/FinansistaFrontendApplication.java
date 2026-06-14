package pl.pb.finansista.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Thymeleaf frontend - a browser-facing app that talks to the backend ONLY over
 * REST. Not yet independently runnable: it still depends on the backend module
 * for compilation, and runtime wiring (component scan over the relocated
 * controllers, excluding the backend's JPA/Flyway auto-config, /me-based auth)
 * is the next step, once the leaks are removed and the backend dependency dropped.
 */
@SpringBootApplication
public class FinansistaFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinansistaFrontendApplication.class, args);
    }
}
