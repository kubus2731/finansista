package pl.pb.finansista.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.common.security.JwtService;
import pl.pb.finansista.frontend.request.view.CreateRequestForm;
import pl.pb.finansista.frontend.request.view.RequestView;
import pl.pb.finansista.request.web.ActivityLogResponse;
import pl.pb.finansista.request.web.AddCommentRequest;
import pl.pb.finansista.request.web.ChangeRequestStatusRequest;
import pl.pb.finansista.request.web.CommentResponse;
import pl.pb.finansista.request.web.CreateRequestRequest;
import pl.pb.finansista.request.web.EditRequestRequest;
import pl.pb.finansista.request.web.RequestResponse;
import pl.pb.finansista.user.exception.UserNotFoundException;
import pl.pb.finansista.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RequestViewController {

    private final UserRepository userRepository;
    private final RestClient backendRestClient;
    private final JwtService jwtService;

    private static final List<String> ALL_STATUSES = List.of(
            "DRAFT", "SUBMITTED", "FORMAL_EVALUATION", "UNDER_REVIEW",
            "ACCEPTED", "REJECTED", "CORRECTION_REQUIRED");

    @GetMapping("/requests")
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String search,
                       HttpServletRequest httpRequest, Model model) {
        // Filtry przekazujemy do backendu tylko gdy mają wartość; resztę robi REST (status/search).
        List<RequestResponse> responses = backendRestClient.get()
                .uri(b -> b.path("/api/v1/requests")
                        .queryParamIfPresent("status", Optional.ofNullable(StringUtils.hasText(status) ? status : null))
                        .queryParamIfPresent("search", Optional.ofNullable(StringUtils.hasText(search) ? search : null))
                        .build())
                .header("Authorization", bearer(httpRequest))
                .retrieve()
                .body(new ParameterizedTypeReference<List<RequestResponse>>() {});

        List<RequestView> views = (responses == null ? List.<RequestResponse>of() : responses)
                .stream().map(this::toView).toList();
        model.addAttribute("requests", views);
        model.addAttribute("statuses", ALL_STATUSES);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchTerm", search);

        return "requests/list";
    }

    @GetMapping("/requests/{id}")
    public String details(@PathVariable UUID id, Authentication authentication,
                          HttpServletRequest httpRequest, Model model) {
        String auth = bearer(httpRequest);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        RequestResponse response = backendRestClient.get()
                .uri("/api/v1/requests/{id}", id)
                .header("Authorization", auth)
                .retrieve()
                .body(RequestResponse.class);

        // Komentarze do wniosku (uwagi jednostek oceniających / wnioskodawcy).
        List<CommentResponse> comments = backendRestClient.get()
                .uri("/api/v1/requests/{id}/comments", id)
                .header("Authorization", auth)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});

        // Dozwolone przejścia statusu dla ZALOGOWANEGO użytkownika - backend zwraca
        // tylko te, do których ma uprawnienia (wnioskodawca w DRAFT: [SUBMITTED],
        // jednostka oceniająca: np. [UNDER_REVIEW, REJECTED, CORRECTION_REQUIRED]).
        List<String> transitions;
        try {
            transitions = backendRestClient.get()
                    .uri("/api/v1/requests/{id}/status/available-transitions", id)
                    .header("Authorization", auth)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});
        } catch (RestClientResponseException e) {
            transitions = List.of();
        }

        // Historia zmian statusu (activity_log) - kto, kiedy i z jakiego na jaki status.
        List<ActivityLogResponse> history;
        try {
            history = backendRestClient.get()
                    .uri("/api/v1/requests/{id}/history", id)
                    .header("Authorization", auth)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ActivityLogResponse>>() {});
        } catch (RestClientResponseException e) {
            history = List.of();
        }

        model.addAttribute("request", toView(response));
        model.addAttribute("req", response);
        model.addAttribute("comments", comments == null ? List.of() : comments);
        model.addAttribute("transitions", transitions == null ? List.of() : transitions);
        model.addAttribute("history", history == null ? List.of() : history);
        // Admin usuwa dowolny wniosek; autor - tylko swój w wersji roboczej (backend i tak waliduje).
        model.addAttribute("canDelete", isAdmin || "DRAFT".equals(response.status()));

        return "requests/details";
    }

    /** Usunięcie wniosku - admin (dowolny) lub autor (własny DRAFT). Autoryzację egzekwuje backend. */
    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable UUID id,
                                HttpServletRequest httpRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            backendRestClient.delete()
                    .uri("/api/v1/requests/{id}", id)
                    .header("Authorization", bearer(httpRequest))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Wniosek został usunięty.");
            return "redirect:/requests";
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie udało się usunąć wniosku (kod " + e.getStatusCode().value() + ").");
            return "redirect:/requests/" + id;
        }
    }

    /** Dodanie komentarza (uwagi) do wniosku przez wnioskodawcę lub jednostkę oceniającą. */
    @PostMapping("/requests/{id}/comments")
    public String addComment(@PathVariable UUID id,
                             @RequestParam String content,
                             HttpServletRequest httpRequest,
                             RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(content)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Treść komentarza nie może być pusta.");
            return "redirect:/requests/" + id;
        }
        try {
            backendRestClient.post()
                    .uri("/api/v1/requests/{id}/comments", id)
                    .header("Authorization", bearer(httpRequest))
                    .body(new AddCommentRequest(content))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Komentarz został dodany.");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się dodać komentarza.");
        }
        return "redirect:/requests/" + id;
    }

    /**
     * Zmiana statusu wniosku - obsługuje zarówno "wyślij wniosek" (DRAFT -> SUBMITTED)
     * jak i procesowanie przez jednostki nadrzędne. Backend wymaga nagłówka If-Match
     * z aktualną wersją encji (optymistyczna blokada), więc najpierw pobieramy świeży
     * ETag z GET, a potem wysyłamy PATCH.
     */
    @PostMapping("/requests/{id}/status")
    public String changeStatus(@PathVariable UUID id,
                               @RequestParam String status,
                               @RequestParam(required = false) String description,
                               HttpServletRequest httpRequest,
                               RedirectAttributes redirectAttributes) {
        String auth = bearer(httpRequest);
        try {
            ResponseEntity<Void> current = backendRestClient.get()
                    .uri("/api/v1/requests/{id}", id)
                    .header("Authorization", auth)
                    .retrieve()
                    .toBodilessEntity();
            String etag = current.getHeaders().getETag();

            backendRestClient.patch()
                    .uri("/api/v1/requests/{id}/status", id)
                    .header("Authorization", auth)
                    .header(HttpHeaders.IF_MATCH, etag)
                    .body(new ChangeRequestStatusRequest(status, description))
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Status wniosku został zmieniony.");
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie udało się zmienić statusu wniosku (kod " + e.getStatusCode().value() + ").");
        }
        return "redirect:/requests/" + id;
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
        model.addAttribute("formAction", "/requests");
        model.addAttribute("editMode", false);
        return "requests/form";
    }

    /**
     * "Złóż ponownie" - otwiera NOWY formularz wypełniony danymi istniejącego wniosku.
     * Przydatne dla cyklicznych przedsięwzięć (np. coroczne wydarzenie). Zapis tworzy
     * nowy wniosek (DRAFT) na koncie składającego, z jego wydziałem - oryginał zostaje.
     */
    @GetMapping("/requests/{id}/duplicate")
    public String duplicateForm(@PathVariable UUID id, Authentication authentication,
                                HttpServletRequest httpRequest, Model model) {
        RequestResponse response = backendRestClient.get()
                .uri("/api/v1/requests/{id}", id)
                .header("Authorization", bearer(httpRequest))
                .retrieve()
                .body(RequestResponse.class);

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        model.addAttribute("form", toForm(response));
        model.addAttribute("departmentName", user.getDepartment().getName());
        model.addAttribute("formAction", "/requests");
        model.addAttribute("editMode", false);
        model.addAttribute("duplicateNotice", true);
        return "requests/form";
    }

    /** Formularz edycji wniosku - dostępny dla autora/admina, gdy status to DRAFT lub CORRECTION_REQUIRED. */
    @GetMapping("/requests/{id}/edit")
    public String editForm(@PathVariable UUID id, Authentication authentication,
                           HttpServletRequest httpRequest, Model model) {
        RequestResponse response = backendRestClient.get()
                .uri("/api/v1/requests/{id}", id)
                .header("Authorization", bearer(httpRequest))
                .retrieve()
                .body(RequestResponse.class);

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        model.addAttribute("form", toForm(response));
        model.addAttribute("departmentName", user.getDepartment().getName());
        model.addAttribute("formAction", "/requests/" + id + "/edit");
        model.addAttribute("editMode", true);
        return "requests/form";
    }

    @PostMapping("/requests/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("form") CreateRequestForm form,
                         BindingResult bindingResult,
                         Authentication authentication,
                         HttpServletRequest httpRequest,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentName", user.getDepartment().getName());
            model.addAttribute("formAction", "/requests/" + id + "/edit");
            model.addAttribute("editMode", true);
            return "requests/form";
        }

        List<String> errors = validateBusinessRules(form);
        if (!errors.isEmpty()) {
            model.addAttribute("departmentName", user.getDepartment().getName());
            model.addAttribute("formAction", "/requests/" + id + "/edit");
            model.addAttribute("editMode", true);
            model.addAttribute("validationErrors", errors);
            return "requests/form";
        }

        List<EditRequestRequest.TaskItem> tasks = form.getTasks().stream()
                .filter(t -> StringUtils.hasText(t.getName()) || t.getPlannedCost() != null)
                .map(t -> new EditRequestRequest.TaskItem(t.getTaskNo(), t.getName(),
                        t.getDateFrom(), t.getDateTo(), t.getPlannedCost(), t.getActions()))
                .toList();

        List<EditRequestRequest.CostItemEntry> costItems = form.getCostItems().stream()
                .filter(c -> StringUtils.hasText(c.getItemName()))
                .map(c -> new EditRequestRequest.CostItemEntry(c.getTaskNo(), c.getItemName(),
                        c.getQuantity(), c.getUnitCost(), c.getNotes()))
                .toList();

        List<EditRequestRequest.FundingEntry> fundings = form.getFundings().stream()
                .filter(f -> f.getAmountRequested() != null)
                .map(f -> new EditRequestRequest.FundingEntry(
                        fundingSourceIdFromName(f.getSourceName()),
                        f.getAmountRequested()))
                .toList();

        EditRequestRequest payload = new EditRequestRequest(
                form.getTitle(), form.getDescription(), form.getAmount(),
                null,
                user.getDepartment().getId(),
                form.getCostCategoryId(),
                form.getRealizerType(), form.getProjectKind(), form.getProjectKindOther(),
                form.getProjectScope(), form.getProjectScopeOther(),
                form.getProjectNature(), form.getProjectNatureOther(),
                form.getPlannedDateFrom(), form.getPlannedDateTo(), form.getLocation(),
                form.getParticipantsInvolved(), form.getParticipantsBenefiting(),
                form.getSupervisorName(), form.getSupervisorEmail(),
                form.getSupervisorPhone(), form.getSupervisorDepartment(),
                tasks, costItems, fundings);

        try {
            String auth = bearer(httpRequest);
            // PUT wymaga If-Match z aktualną wersją - pobieramy świeży ETag z GET.
            ResponseEntity<Void> current = backendRestClient.get()
                    .uri("/api/v1/requests/{id}", id)
                    .header("Authorization", auth)
                    .retrieve()
                    .toBodilessEntity();
            String etag = current.getHeaders().getETag();

            backendRestClient.put()
                    .uri("/api/v1/requests/{id}", id)
                    .header("Authorization", auth)
                    .header(HttpHeaders.IF_MATCH, etag)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("successMessage", "Wniosek został zaktualizowany.");
            return "redirect:/requests/" + id;
        } catch (RestClientResponseException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie udało się zapisać zmian (kod " + e.getStatusCode().value() + ").");
            return "redirect:/requests/" + id + "/edit";
        }
    }

    /** Mapuje wniosek z REST na formularz edycji (odtwarza 4 standardowe wiersze źródeł po ID). */
    private CreateRequestForm toForm(RequestResponse r) {
        CreateRequestForm form = new CreateRequestForm();
        form.setTitle(r.title());
        form.setDescription(r.description());
        form.setAmount(r.amount());
        form.setCostCategoryId(r.costCategoryId());
        form.setRealizerType(r.realizerType());
        form.setProjectKind(r.projectKind());
        form.setProjectKindOther(r.projectKindOther());
        form.setProjectScope(r.projectScope());
        form.setProjectScopeOther(r.projectScopeOther());
        form.setProjectNature(r.projectNature());
        form.setProjectNatureOther(r.projectNatureOther());
        form.setPlannedDateFrom(r.plannedDateFrom());
        form.setPlannedDateTo(r.plannedDateTo());
        form.setLocation(r.location());
        form.setParticipantsInvolved(r.participantsInvolved());
        form.setParticipantsBenefiting(r.participantsBenefiting());
        form.setSupervisorName(r.supervisorName());
        form.setSupervisorEmail(r.supervisorEmail());
        form.setSupervisorPhone(r.supervisorPhone());
        form.setSupervisorDepartment(r.supervisorDepartment());

        // Harmonogram - istniejące pozycje, dopełnione do min. 2 wierszy.
        List<RequestResponse.TaskResponse> srcTasks = r.tasks() == null ? List.of() : r.tasks();
        for (RequestResponse.TaskResponse t : srcTasks) {
            CreateRequestForm.TaskRow row = new CreateRequestForm.TaskRow();
            row.setTaskNo(t.taskNo());
            row.setName(t.name());
            row.setDateFrom(t.dateFrom());
            row.setDateTo(t.dateTo());
            row.setPlannedCost(t.plannedCost());
            row.setActions(t.actions());
            form.getTasks().add(row);
        }
        for (int i = form.getTasks().size(); i < 2; i++) {
            form.getTasks().add(taskRow(i + 1));
        }

        // Kosztorys - istniejące pozycje, dopełnione do min. 4 wierszy.
        List<RequestResponse.CostItemResponse> srcCosts = r.costItems() == null ? List.of() : r.costItems();
        for (RequestResponse.CostItemResponse c : srcCosts) {
            CreateRequestForm.CostItemRow row = new CreateRequestForm.CostItemRow();
            row.setTaskNo(c.taskNo());
            row.setItemName(c.itemName());
            row.setQuantity(c.quantity());
            row.setUnitCost(c.unitCost());
            row.setNotes(c.notes());
            form.getCostItems().add(row);
        }
        for (int i = form.getCostItems().size(); i < 4; i++) {
            form.getCostItems().add(new CreateRequestForm.CostItemRow());
        }

        // Źródła finansowania - 4 stałe wiersze, kwoty z istniejących wpisów po fundingSourceId.
        form.getFundings().add(fundingRowWithAmount("Samorząd Studentów PB", 1L, r));
        form.getFundings().add(fundingRowWithAmount("Samorząd Doktorantów PB", 2L, r));
        form.getFundings().add(fundingRowWithAmount("Inicjatywy kół naukowych / organizacji", 3L, r));
        form.getFundings().add(fundingRowWithAmount("Środki Wydziału", 4L, r));

        return form;
    }

    private CreateRequestForm.FundingRow fundingRowWithAmount(String sourceName, Long sourceId, RequestResponse r) {
        CreateRequestForm.FundingRow row = new CreateRequestForm.FundingRow();
        row.setSourceName(sourceName);
        if (r.fundings() != null) {
            r.fundings().stream()
                    .filter(f -> sourceId.equals(f.fundingSourceId()))
                    .findFirst()
                    .ifPresent(f -> row.setAmountRequested(f.amountRequested()));
        }
        return row;
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

        List<String> errors = validateBusinessRules(form);
        if (!errors.isEmpty()) {
            model.addAttribute("departmentName", user.getDepartment().getName());
            model.addAttribute("validationErrors", errors);
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
                .map(f -> new CreateRequestRequest.FundingEntry(
                        fundingSourceIdFromName(f.getSourceName()),
                        f.getAmountRequested()))
                .toList();

        CreateRequestRequest payload = new CreateRequestRequest(
                form.getTitle(), form.getDescription(), form.getAmount(),
                null,
                user.getDepartment().getId(),
                form.getCostCategoryId(),
                form.getRealizerType(), form.getProjectKind(), form.getProjectKindOther(),
                form.getProjectScope(), form.getProjectScopeOther(),
                form.getProjectNature(), form.getProjectNatureOther(),
                form.getPlannedDateFrom(), form.getPlannedDateTo(), form.getLocation(),
                form.getParticipantsInvolved(), form.getParticipantsBenefiting(),
                form.getSupervisorName(), form.getSupervisorEmail(),
                form.getSupervisorPhone(), form.getSupervisorDepartment(),
                tasks, costItems, fundings);

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

    private RequestView toView(RequestResponse r) {
        return new RequestView(
                r.id(),
                r.title(),
                r.amount(),
                r.status(),
                r.departmentName(),
                r.applicantName(),
                r.createdAt().toLocalDate()
        );
    }

    /**
     * Mapuje wyświetlaną nazwę źródła finansowania na ID ze słownika funding_source (V2).
     * Tymczasowe rozwiązanie - docelowo formularz powinien przechowywać ID wprost.
     */
    private Long fundingSourceIdFromName(String sourceName) {
        if (sourceName == null) return null;
        return switch (sourceName) {
            case "Samorząd Studentów PB" -> 1L;
            case "Samorząd Doktorantów PB" -> 2L;
            case "Inicjatywy kół naukowych / organizacji" -> 3L;
            case "Środki Wydziału" -> 4L;
            default -> null;
        };
    }

    private String bearer(HttpServletRequest request) {
        return "Bearer " + jwtService.getJwtFromCookies(request);
    }

    private List<String> validateBusinessRules(CreateRequestForm form) {
        List<String> errors = new ArrayList<>();

        if (form.getPlannedDateFrom() != null && form.getPlannedDateTo() != null
                && form.getPlannedDateTo().isBefore(form.getPlannedDateFrom())) {
            errors.add("Termin „do” nie może być wcześniejszy niż termin „od”.");
        }

        for (CreateRequestForm.TaskRow t : form.getTasks()) {
            if (t.getDateFrom() != null && t.getDateTo() != null && t.getDateTo().isBefore(t.getDateFrom())) {
                errors.add("Zadanie nr " + t.getTaskNo() + ": data zakończenia jest wcześniejsza niż rozpoczęcia.");
            }
            if (t.getPlannedCost() != null && t.getPlannedCost().signum() <= 0) {
                errors.add("Zadanie nr " + t.getTaskNo() + ": planowany koszt musi być dodatni.");
            }
        }

        for (CreateRequestForm.CostItemRow c : form.getCostItems()) {
            if (c.getUnitCost() != null && c.getUnitCost().signum() <= 0) {
                errors.add("Kosztorys: koszt pozycji „" + c.getItemName() + "” musi być dodatni.");
            }
            if (c.getQuantity() != null && c.getQuantity() <= 0) {
                errors.add("Kosztorys: ilość pozycji „" + c.getItemName() + "” musi być dodatnia.");
            }
        }

        boolean anyFunding = false;
        BigDecimal fundingSum = BigDecimal.ZERO;
        for (CreateRequestForm.FundingRow f : form.getFundings()) {
            if (f.getAmountRequested() != null) {
                anyFunding = true;
                if (f.getAmountRequested().signum() <= 0) {
                    errors.add("Źródło „" + f.getSourceName() + "”: kwota wnioskowana musi być dodatnia.");
                } else {
                    fundingSum = fundingSum.add(f.getAmountRequested());
                }
            }
        }
        if (anyFunding && form.getAmount() != null && fundingSum.compareTo(form.getAmount()) != 0) {
            errors.add("Suma kwot ze źródeł (" + fundingSum + " zł) musi równać się łącznej wnioskowanej kwocie ("
                    + form.getAmount() + " zł).");
        }

        return errors;
    }
}