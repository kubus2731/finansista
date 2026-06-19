package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.Procedure;
import pl.pb.finansista.request.Request;

interface SpringDataJpaRequestRepository
    extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

  @Procedure(procedureName = "finansista_pkg.set_actor")
  void setActor(Long userId);

  @NonNull
  @EntityGraph(attributePaths = {"status", "department", "costCategory", "template"})
  List<Request> findAll(@Nullable Specification<Request> spec);

  Optional<Request> findByExternalId(UUID externalId);
}
