package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.RequestTemplate;
import java.util.UUID;

interface SpringDataJpaRequestTemplateRepository extends JpaRepository<RequestTemplate, UUID> {
}
