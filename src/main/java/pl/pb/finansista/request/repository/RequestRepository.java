package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.Request;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public interface RequestRepository {

    Optional<Request> findById(UUID id);

    List<Request> findAll(Specification<Request> spec);

    Request save(Request request);
}
