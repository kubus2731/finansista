package pl.pb.finansista.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.common.security.JwtService;
import pl.pb.finansista.request.Request;
import pl.pb.finansista.request.usecase.*;
import pl.pb.finansista.request.view.CreateRequestForm;
import pl.pb.finansista.request.view.RequestView;
import pl.pb.finansista.request.web.CreateRequestRequest;
import pl.pb.finansista.request.web.RequestResponse;
import pl.pb.finansista.user.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RequestViewController {

    private final GetSingleRequestUseCase getSingleRequestUseCase;
    private final CreateRequestUseCase createRequestUseCase;
    private final UserRepository userRepository;
    private final RestClient backendRestClient;
    private final JwtService jwtService;

    @GetMapping("/requests")
    public String list(HttpServletRequest httpRequest, Model model) {
        List<RequestResponse> responses = backendRestClient.get()
                .uri("/api/v1/requests")
                .header("Authorization", bearer(httpRequest))
                .retrieve()
                .body(new ParameterizedTypeReference<List<RequestResponse>>() {});

        List<RequestView> views = (responses == null ? List.<RequestResponse>of() : responses)
                .stream().map(this::toView).toList();
        model.addAttribute("requests", views);

        return "requests/list";
    }

    @GetMapping("/requests/{id}")
    public String details(@PathVariable UUID id, HttpServletRequest httpRequest, Model model) {
        // szczegóły także przez REST; autoryzacja (czyj wniosek / admin) dzieje się po stronie backendu
        RequestResponse response = backendRestClient.get()
                .uri("/api/v1/requests/{id}", id)
                .header("Authorization", bearer(httpRequest))
                .retrieve()
                .body(RequestResponse.class);

        model.addAttribute("request", toView(response));
        model.addAttribute("req", response);

        return "requests/details";
    }

    @GetMapping("/requests/new")
    public String createForm(Authentication authentication, Model model) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        model.addAttribute("departmentName", user.getDepartment().getName());

        CreateRequestForm form = new CreateRequestForm();
        form.getTasks().add(taskRow(1));
        form.getTasks().add(taskRow(2));
        for (int i = 0; i < 4; i++) {
            form.getCostItems().add(new CreateRequestForm.CostItemRow());
        }
        form.getFundings().add(fundingRow("Samorząd Studentów PB"));
        form.getFundings().add(fundingRow("Samorząd Doktorantów PB"));
        form.getFundings().add(fundingRow("Inicjatywy kół naukowych / organizacji"));
        form.getFundings().add(fundingRow("Środki Wydziału"));

        model.addAttribute("form", form);
        return "requests/form";
    }

    @PostMapping("/requests")
    public String create(@Valid @ModelAttribute("form") CreateRequestForm form,
                         BindingResult bindingResult,
                         Authentication authentication,
                         HttpServletRequest httpRequest,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentName", user.getDepartment().getName());
            return "requests/form";
        }

        List<CreateRequestRequest.TaskItem> tasks = form.getTasks().stream()
                .filter(t -> StringUtils.hasText(t.getName()) || t.getPlannedCost() != null)
                .map(t -> new CreateRequestRequest.TaskItem(t.getTaskNo(), t.getName(),
                        t.getDateFrom(), t.getDateTo(), t.getPlannedCost(), t.getActions()))
                .toList();

        List<CreateRequestRequest.CostItemEntry> costItems = form.getCostItems().stream()
                .filter(c -> StringUtils.hasText(c.getItemName()))
                .map(c -> new CreateRequestRequest.CostItemEntry(c.getTaskNo(), c.getItemName(),
                        c.getQuantity(), c.getUnitCost(), c.getNotes()))
                .toList();

        List<CreateRequestRequest.FundingEntry> fundings = form.getFundings().stream()
                .filter(f -> f.getAmountRequested() != null)
                .map(f -> new CreateRequestRequest.FundingEntry(f.getSourceName(),
                        f.getAmountRequested(), f.getAmountGranted()))
                .toList();

        // dział z konta zalogowanego usera (reguła "wydział z serwera")
        CreateRequestRequest payload = new CreateRequestRequest(
                form.getTitle(), form.getDescription(), form.getAmount(),
                null,
                user.getDepartment().getId(),
                form.getCostCategoryId(),
                null,
                form.getRealizerType(), form.getProjectKind(), form.getProjectKindOther(),
                form.getProjectScope(), form.getProjectScopeOther(),
                form.getProjectNature(), form.getProjectNatureOther(),
                form.getPlannedDateFrom(), form.getPlannedDateTo(), form.getLocation(),
                form.getParticipantsInvolved(), form.getParticipantsBenefiting(),
                form.getSupervisorName(), form.getSupervisorEmail(),
                form.getSupervisorPhone(), form.getSupervisorDepartment(),
                tasks, costItems, fundings);

        // zapis przez REST API, nie przez use case
        backendRestClient.post()
                .uri("/api/v1/requests")
                .header("Authorization", bearer(httpRequest))
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        redirectAttributes.addFlashAttribute("successMessage", "Wniosek został zapisany jako wersja robocza.");
        return "redirect:/requests";
    }

    private CreateRequestForm.TaskRow taskRow(int no) {
        CreateRequestForm.TaskRow row = new CreateRequestForm.TaskRow();
        row.setTaskNo(no);
        return row;
    }

    private CreateRequestForm.FundingRow fundingRow(String sourceName) {
        CreateRequestForm.FundingRow row = new CreateRequestForm.FundingRow();
        row.setSourceName(sourceName);
        return row;
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

    private RequestView toView(RequestResponse r) {
        return new RequestView(
                r.externalId(),
                r.title(),
                r.amount(),
                r.status(),
                r.departmentName(),
                r.applicantName(),
                r.createdAt().toLocalDate()
        );
    }

    private String bearer(HttpServletRequest request) {
        return "Bearer " + jwtService.getJwtFromCookies(request);
    }

    private boolean hasAdminOrDeanRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_DEAN_OFFICE"));
    }
}