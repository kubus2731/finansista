package pl.pb.finansista.request.repository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.Request;
import java.util.Optional;
import java.util.UUID;
@Repository
class JpaRequestRepository implements RequestRepository {

    private final SpringDataJpaRequestRepository repository;

    public JpaRequestRepository(SpringDataJpaRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Request> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Request save(Request request) {
        return repository.save(request);
    }
}
