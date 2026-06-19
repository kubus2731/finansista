package pl.pb.finansista.request.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

@Service
@RequiredArgsConstructor
public class CreateRequestTemplateUseCase {

  private final RequestTemplateRepository requestTemplateRepository;

  @Transactional
  public RequestTemplate execute(String title, String description) {
    RequestTemplate template = new RequestTemplate(title, description);
    return requestTemplateRepository.save(template);
  }
}
