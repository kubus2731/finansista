package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Request;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
class JpaRequestRepository implements RequestRepository {

    private final SpringDataJpaRequestRepository repository;

    public JpaRequestRepository(SpringDataJpaRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Request> findByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId);
    }

    @Override
    public Optional<Request> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Request> findAll(Specification<Request> spec) {
        return repository.findAll(spec);
    }

    @Override
    public Request save(Request request) {
        return repository.save(request);
    }

    @Override
    public void delete(Request request) {
        repository.delete(request);
    }

    @Override
    public void setActor(long id) {
        repository.setActor(id);
    }
}
