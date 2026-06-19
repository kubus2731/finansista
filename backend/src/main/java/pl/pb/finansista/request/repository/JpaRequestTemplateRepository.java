package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.RequestTemplate;

@Repository
class JpaRequestTemplateRepository implements RequestTemplateRepository {

  private final SpringDataJpaRequestTemplateRepository repository;

  public JpaRequestTemplateRepository(SpringDataJpaRequestTemplateRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<RequestTemplate> findByExternalId(UUID externalId) {
    return repository.findByExternalId(externalId);
  }

  @Override
  public RequestTemplate save(RequestTemplate requestTemplate) {
    return repository.save(requestTemplate);
  }

  @Override
  public List<RequestTemplate> findActiveTemplates() {
    return repository.findByActiveTrue();
  }

  @Override
  public List<RequestTemplate> findAll() {
    return repository.findAll();
  }

  @Override
  public void delete(RequestTemplate requestTemplate) {
    repository.delete(requestTemplate);
  }
}
