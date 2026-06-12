package pl.pb.finansista.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.pb.finansista.request.service.RequestService;
import pl.pb.finansista.request.view.CreateRequestForm;
import pl.pb.finansista.request.view.RequestView;

import java.util.UUID;

@Controller
@Profile("mock")
@RequiredArgsConstructor
public class RequestViewController {

    private final RequestService requestService;

    @GetMapping("/requests")
    public String list(Model model) {
        model.addAttribute("requests", requestService.findAll());
        return "requests/list";
    }

    @GetMapping("/requests/{id}")
    public String details(@PathVariable UUID id, Model model) {
        RequestView request = requestService.findById(id).orElseThrow();
        model.addAttribute("request", request);
        return "requests/details";
    }

    @GetMapping("/requests/new")
    public String createForm(Model model){
        model.addAttribute("form", new CreateRequestForm(null, null, null, null));
        return "requests/form";
    }

    @PostMapping("/requests")
    public String create(@Valid @ModelAttribute("form") CreateRequestForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "requests/form";
        }

        requestService.create(form);

        redirectAttributes.addFlashAttribute("successMessage","Wniosek został zapisany jako wersja robocza.");
        return "redirect:/requests";
    }
}
