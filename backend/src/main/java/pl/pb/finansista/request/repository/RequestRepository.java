package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.domain.Specification;
import pl.pb.finansista.request.Request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository {

    Optional<Request> findById(Long id);

    Optional<Request> findByExternalId(UUID externalId);

    Optional<Request> findOne(Specification<Request> spec);

    List<Request> findAll(Specification<Request> spec);

    Page<Request> findAll(Specification<Request> spec, Pageable pageable);

    Request save(Request request);

    /** Wymusza wykonanie oczekujących operacji SQL (np. DELETE po wyczyszczeniu kolekcji). */
    void flush();

    void delete(Request request);

    void setActor(long Id);
}
