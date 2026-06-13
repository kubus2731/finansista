package pl.pb.finansista.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.pb.finansista.common.security.JwtService;
import pl.pb.finansista.user.InvalidCredentialsException;
import pl.pb.finansista.user.User;
import pl.pb.finansista.user.UserAlreadyExistsException;
import pl.pb.finansista.user.usecase.LoginUserCommand;
import pl.pb.finansista.user.usecase.LoginUserUseCase;
import pl.pb.finansista.user.usecase.RegisterUserUseCase;
import pl.pb.finansista.user.web.RegisterUserRequest;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {
        try {
            User user = loginUserUseCase.execute(new LoginUserCommand(email, password));
            setJwtCookie(response, user);
            return "redirect:/requests";
        } catch (InvalidCredentialsException | IllegalArgumentException e) {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterUserRequest form,
                           BindingResult bindingResult,
                           HttpServletResponse response,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Sprawdź poprawność wprowadzonych danych.");
            return "auth/register";
        }
        try {
            User user = registerUserUseCase.execute(form.toCommand());
            setJwtCookie(response, user);   // automatyczne zalogowanie po rejestracji
            return "redirect:/requests";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("errorMessage", "Konto z tym adresem e-mail lub telefonem już istnieje.");
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, jwtService.getCleanJwtCookie().toString());
        return "redirect:/login?logout";
    }

    private void setJwtCookie(HttpServletResponse response, User user) {
        String token = jwtService.generateToken(user);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtService.generateJwtCookie(token).toString());
    }
}
