package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Request;

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
  public Optional<Request> findOne(Specification<Request> spec) {
    return repository.findOne(spec);
  }

  @Override
  public List<Request> findAll(Specification<Request> spec) {
    return repository.findAll(spec);
  }

  @Override
  public Page<Request> findAll(Specification<Request> spec, Pageable pageable) {
    return repository.findAll(spec, pageable);
  }

  @Override
  public Request save(Request request) {
    return repository.save(request);
  }

  @Override
  public void flush() {
    repository.flush();
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
