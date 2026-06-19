package pl.pb.finansista.frontend.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import pl.pb.finansista.frontend.user.view.RegisterForm;
import pl.pb.finansista.frontend.viewmodel.LoginUserRequest;
import pl.pb.finansista.frontend.viewmodel.RegisterUserRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final RestClient backendRestClient;

    private static final Long STUDENT_ROLE_ID = 2L;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {
        try {
            ResponseEntity<Void> backendResponse = backendRestClient.post()
                    .uri("/api/v1/auth/login")
                    .body(new LoginUserRequest(email, password))
                    .retrieve()
                    .toBodilessEntity();
            relayCookies(backendResponse, response);
            return "redirect:/requests";
        } catch (RestClientResponseException e) {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form,
                           BindingResult bindingResult,
                           HttpServletResponse response,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Sprawdź poprawność wprowadzonych danych (np. telefon musi mieć 9 cyfr, hasło minimum 8 znaków).");
            return "auth/register";
        }
        if (!form.rawPassword().equals(form.passwordConfirm())) {
            model.addAttribute("errorMessage", "Podane hasła nie są identyczne.");
            return "auth/register";
        }
        try {
            RegisterUserRequest payload = new RegisterUserRequest(
                    form.name(), form.surname(), form.email(), form.phoneNumber(),
                    form.rawPassword(), STUDENT_ROLE_ID, form.departmentId());

            ResponseEntity<Void> backendResponse = backendRestClient.post()
                    .uri("/api/v1/auth/register")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            relayCookies(backendResponse, response);
            return "redirect:/requests";
        } catch (RestClientResponseException e) {
            model.addAttribute("errorMessage", "Konto z tym adresem e-mail lub telefonem już istnieje.");
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        ResponseEntity<Void> backendResponse = backendRestClient.post()
                .uri("/api/v1/auth/logout")
                .retrieve()
                .toBodilessEntity();
        relayCookies(backendResponse, response);
        return "redirect:/login?logout";
    }

    private void relayCookies(ResponseEntity<?> backendResponse, HttpServletResponse response) {
        List<String> cookies = backendResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            cookies.forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
        }
    }
}
