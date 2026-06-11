package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;
import java.util.List;
import java.util.UUID;

interface SpringDataJpaRequestRepository extends JpaRepository<Request, UUID>, JpaSpecificationExecutor<Request> {
    @EntityGraph(attributePaths = {"status", "department", "costCategory", "fundingSource", "template"})
    List<Request> findAll(Specification<Request> spec);
}
