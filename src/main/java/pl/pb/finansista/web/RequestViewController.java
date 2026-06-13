package pl.pb.finansista.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.*;
import pl.pb.finansista.request.view.CreateRequestForm;
import pl.pb.finansista.request.view.RequestView;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RequestViewController {

    private final GetRequestsUseCase getRequestsUseCase;
    private final GetSingleRequestUseCase getSingleRequestUseCase;
    private final CreateRequestUseCase createRequestUseCase;
    private final UserRepository userRepository;

    @GetMapping("/requests")
    public String list(Authentication authentication, Model model) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        GetRequestsQuery query = new GetRequestsQuery(authentication.getName(), authorities, null, null);
        List<Request> requests = getRequestsUseCase.execute(query);
        List<RequestView> views = requests.stream().map(this::toView).toList();
        model.addAttribute("requests", views);

        return "requests/list";
    }

    @GetMapping("/requests/{id}")
    public String details(@PathVariable UUID id, Authentication authentication, Model model) {
        boolean isAdminOrDean = hasAdminOrDeanRole(authentication);

        GetSingleRequestQuery query = new GetSingleRequestQuery(id, authentication.getName(), isAdminOrDean);
        Request request = getSingleRequestUseCase.execute(query);
        model.addAttribute("request", toView(request));


        return "requests/details";
    }

    @GetMapping("/requests/new")
    public String createForm(Authentication authentication, Model model) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        model.addAttribute("departmentName", user.getDepartment().getName());
        model.addAttribute("form", new CreateRequestForm(null, null, null, null));
        return "requests/form";
    }

    @PostMapping("/requests")
    public String create(@Valid @ModelAttribute("form") CreateRequestForm form,
                         BindingResult bindingResult,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "requests/form";
        }

        // dział bierzemy z konta zalogowanego usera (reguła: "wydział z serwera")
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Long departmentId = user.getDepartment().getId();

        CreateRequestCommand command = new CreateRequestCommand(
                form.title(), form.description(), form.amount(),
                authentication.getName(),
                null,
                departmentId,
                form.costCategoryId(),
                null);

        createRequestUseCase.execute(command);

        redirectAttributes.addFlashAttribute("successMessage", "Wniosek został zapisany jako wersja robocza.");
        return "redirect:/requests";
    }

    private RequestView toView(Request r) {
        return new RequestView(
                r.getExternalId(),
                r.getTitle(),
                r.getAmount(),
                r.getStatus().getName(),
                r.getDepartment().getName(),
                r.getUser().getName() + " " + r.getUser().getSurname(),
                r.getCreatedAt().toLocalDate()
        );
    }

    private boolean hasAdminOrDeanRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_DEAN_OFFICE"));
    }
}