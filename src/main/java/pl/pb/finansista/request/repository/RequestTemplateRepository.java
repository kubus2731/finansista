package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.RequestTemplate;
import java.util.Optional;
import java.util.UUID;

public interface RequestTemplateRepository {

    Optional<RequestTemplate> findById(Long id);

    RequestTemplate save(RequestTemplate requestTemplate);
}
