package pl.pb.finansista.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Klient HTTP, przez który warstwa frontowa (kontrolery Thymeleaf) komunikuje się
 * z backendem WYŁĄCZNIE przez REST API - zgodnie z deklaracją (frontend niezależny,
 * REST jako most). Adres backendu jest konfigurowalny, więc front można w każdej
 * chwili odłączyć do osobnej aplikacji.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient backendRestClient(@Value("${app.backend.url:http://localhost:8080}") String backendUrl) {
        return RestClient.builder()
                .baseUrl(backendUrl)
                .build();
    }
}
