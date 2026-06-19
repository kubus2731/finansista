package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.RequestTemplate;

interface SpringDataJpaRequestTemplateRepository extends JpaRepository<RequestTemplate, Long> {

  List<RequestTemplate> findByActiveTrue();

  Optional<RequestTemplate> findByExternalId(UUID externalId);
}
