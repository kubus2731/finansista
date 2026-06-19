package pl.pb.finansista.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.frontend.viewmodel.ReferenceResponse;
import pl.pb.finansista.frontend.viewmodel.ChangeUserActiveRequest;
import pl.pb.finansista.frontend.viewmodel.ChangeUserDepartmentRequest;
import pl.pb.finansista.frontend.viewmodel.ChangeUserRoleRequest;
import pl.pb.finansista.frontend.viewmodel.RoleResponse;
import pl.pb.finansista.frontend.viewmodel.UserResponse;
import org.springframework.web.util.WebUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Panel administratora - zarządzanie użytkownikami (zmiana roli i działu).
 * Cała komunikacja idzie przez REST backendu. Dostęp ograniczony do ROLE_ADMIN
 * na poziomie metody (EnableMethodSecurity), niezależnie od backendowego
 * @PreAuthorize - dzięki temu nie-admin dostaje stronę 403 zamiast błędu 500
 * z odrzuconego wywołania REST.
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {

    private final RestClient backendRestClient;

    @Value("${app.security.jwt.cookie-name:jwt}")
    private String jwtCookieName;

    @GetMapping("/admin/users")
    public String users(HttpServletRequest httpRequest, Model model) {
        String auth = bearer(httpRequest);

        List<UserResponse> users = backendRestClient.get()
                .uri("/api/v1/users")
                .header("Authorization", auth)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserResponse>>() {});

        List<RoleResponse> roles = backendRestClient.get()
                .uri("/api/v1/roles")
                .header("Authorization", auth)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RoleResponse>>() {});

        List<ReferenceResponse> departments = backendRestClient.get()
                .uri("/api/v1/reference/departments")
                .header("Authorization", auth)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReferenceResponse>>() {});

        model.addAttribute("users", users == null ? List.of() : users);
        model.addAttribute("roles", roles == null ? List.of() : roles);
        model.addAttribute("departments", departments == null ? List.of() : departments);

        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String changeRole(@PathVariable("id") String userId,
                             @RequestParam Long roleId,
                             HttpServletRequest httpRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            backendRestClient.patch()
                    .uri("/api/v1/users/{id}/role", userId)
                    .header("Authorization", bearer(httpRequest))
                    .body(new ChangeUserRoleRequest(roleId))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Rola użytkownika została zmieniona.");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się zmienić roli użytkownika.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/department")
    public String changeDepartment(@PathVariable("id") String userId,
                                   @RequestParam Long departmentId,
                                   HttpServletRequest httpRequest,
                                   RedirectAttributes redirectAttributes) {
        try {
            backendRestClient.patch()
                    .uri("/api/v1/users/{id}/department", userId)
                    .header("Authorization", bearer(httpRequest))
                    .body(new ChangeUserDepartmentRequest(departmentId))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Dział użytkownika został zmieniony.");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się zmienić działu użytkownika.");
        }
        return "redirect:/admin/users";
    }

    /** Soft delete: dezaktywacja / ponowna aktywacja konta użytkownika. */
    @PostMapping("/admin/users/{id}/active")
    public String setActive(@PathVariable("id") String userId,
                            @RequestParam boolean active,
                            HttpServletRequest httpRequest,
                            RedirectAttributes redirectAttributes) {
        try {
            backendRestClient.patch()
                    .uri("/api/v1/users/{id}/active", userId)
                    .header("Authorization", bearer(httpRequest))
                    .body(new ChangeUserActiveRequest(active))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage",
                    active ? "Konto użytkownika zostało aktywowane." : "Konto użytkownika zostało dezaktywowane.");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się zmienić stanu konta użytkownika.");
        }
        return "redirect:/admin/users";
    }

    private String bearer(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        return "Bearer " + (cookie != null ? cookie.getValue() : "");
    }
}
