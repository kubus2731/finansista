package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository {

    Optional<Request> findById(Long id);

    Optional<Request> findByExternalId(UUID externalId);

    List<Request> findAll(Specification<Request> spec);

    Request save(Request request);

    void delete(Request request);

    void setActor(long Id);
}
