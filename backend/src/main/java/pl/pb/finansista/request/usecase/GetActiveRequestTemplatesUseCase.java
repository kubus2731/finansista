package pl.pb.finansista.request.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pb.finansista.request.RequestTemplate;
import pl.pb.finansista.request.repository.RequestTemplateRepository;

@Service
@RequiredArgsConstructor
public class GetActiveRequestTemplatesUseCase {

  private final RequestTemplateRepository requestTemplateRepository;

  @Transactional(readOnly = true)
  public List<RequestTemplate> execute() {
    return requestTemplateRepository.findActiveTemplates();
  }
}
