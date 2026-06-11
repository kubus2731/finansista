package pl.pb.finansista.request.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;
import java.util.List;

interface SpringDataJpaRequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    @EntityGraph(attributePaths = {"status", "department", "costCategory", "fundingSource", "template"})
    List<Request> findAll(Specification<Request> spec);

    java.util.Optional<Request> findByExternalId(UUID externalId);
}
