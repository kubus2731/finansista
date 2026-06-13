package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditRequestTemplateUseCase {

    private final RequestTemplateRepository requestTemplateRepository;

    @Transactional
    public RequestTemplate execute(UUID id, String title, String description, boolean active) {
        RequestTemplate template = requestTemplateRepository.findByExternalId(id)
                .orElseThrow(RequestTemplateNotFoundException::new);

        template.updateDetails(title, description);
        
        if (active && !template.isActive()) {
            template.activate();
        } else if (!active && template.isActive()) {
            template.deactivate();
        }

        return requestTemplateRepository.save(template);
    }
}
