package pl.pb.finansista.request.repository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.RequestStatus;
import java.util.Optional;
import java.util.UUID;
@Repository
class JpaRequestStatusRepository implements RequestStatusRepository {

    private final SpringDataJpaRequestStatusRepository repository;

    public JpaRequestStatusRepository(SpringDataJpaRequestStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RequestStatus> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public RequestStatus save(RequestStatus requestStatus) {
        return repository.save(requestStatus);
    }

    @Override
    public Optional<RequestStatus> findByName(String name) {
        return repository.findByName(name);
    }
}
