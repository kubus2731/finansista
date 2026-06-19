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
public class DeleteRequestTemplateUseCase {

  private final RequestTemplateRepository requestTemplateRepository;

  @Transactional
  public void execute(UUID id) {
    RequestTemplate template =
        requestTemplateRepository
            .findByExternalId(id)
            .orElseThrow(RequestTemplateNotFoundException::new);
    requestTemplateRepository.delete(template);
  }
}
