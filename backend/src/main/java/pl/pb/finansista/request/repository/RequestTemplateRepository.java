package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import pl.pb.finansista.request.RequestTemplate;

public interface RequestTemplateRepository {

  Optional<RequestTemplate> findByExternalId(UUID externalId);

  List<RequestTemplate> findActiveTemplates();

  List<RequestTemplate> findAll();

  RequestTemplate save(RequestTemplate requestTemplate);

  void delete(RequestTemplate requestTemplate);
}
