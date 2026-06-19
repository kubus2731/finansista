package pl.pb.finansista.request.usecase;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.exception.RequestTemplateNotFoundException;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

@Service
@RequiredArgsConstructor
public class EditRequestTemplateUseCase {

  private final RequestTemplateRepository requestTemplateRepository;

  @Transactional
  public RequestTemplate execute(
      UUID id, String title, String description, boolean active, Long version) {
    RequestTemplate template =
        requestTemplateRepository
            .findByExternalId(id)
            .orElseThrow(RequestTemplateNotFoundException::new);

    template.assertVersion(version);

    template.updateDetails(title, description);

    if (active && !template.isActive()) {
      template.activate();
    } else if (!active && template.isActive()) {
      template.deactivate();
    }

    return requestTemplateRepository.save(template);
  }
}
