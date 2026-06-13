package pl.pb.finansista.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
        model.addAttribute("req", request);

        return "requests/details";
    }

    @GetMapping("/requests/new")
    public String createForm(Authentication authentication, Model model) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        model.addAttribute("departmentName", user.getDepartment().getName());

        CreateRequestForm form = new CreateRequestForm();
        // harmonogram: 2 puste zadania (jak w papierowym Załączniku)
        form.getTasks().add(taskRow(1));
        form.getTasks().add(taskRow(2));
        // kosztorys: 4 puste pozycje
        for (int i = 0; i < 4; i++) {
            form.getCostItems().add(new CreateRequestForm.CostItemRow());
        }
        // źródła finansowania: 4 stałe wiersze z sekcji VI Załącznika
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
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // dział bierzemy z konta zalogowanego usera (reguła: "wydział z serwera")
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentName", user.getDepartment().getName());
            return "requests/form";
        }

        // odsiewamy puste wiersze, których user nie wypełnił
        List<CreateRequestCommand.TaskData> tasks = form.getTasks().stream()
                .filter(t -> StringUtils.hasText(t.getName()) || t.getPlannedCost() != null)
                .map(t -> new CreateRequestCommand.TaskData(t.getTaskNo(), t.getName(),
                        t.getDateFrom(), t.getDateTo(), t.getPlannedCost(), t.getActions()))
                .toList();

        List<CreateRequestCommand.CostItemData> costItems = form.getCostItems().stream()
                .filter(c -> StringUtils.hasText(c.getItemName()))
                .map(c -> new CreateRequestCommand.CostItemData(c.getTaskNo(), c.getItemName(),
                        c.getQuantity(), c.getUnitCost(), c.getNotes()))
                .toList();

        List<CreateRequestCommand.FundingData> fundings = form.getFundings().stream()
                .filter(f -> f.getAmountRequested() != null)
                .map(f -> new CreateRequestCommand.FundingData(f.getSourceName(),
                        f.getAmountRequested(), f.getAmountGranted()))
                .toList();

        CreateRequestCommand command = new CreateRequestCommand(
                form.getTitle(), form.getDescription(), form.getAmount(),
                authentication.getName(),
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

        createRequestUseCase.execute(command);

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

    private boolean hasAdminOrDeanRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_DEAN_OFFICE"));
    }
}