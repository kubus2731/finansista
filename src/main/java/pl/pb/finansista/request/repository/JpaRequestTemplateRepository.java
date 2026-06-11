package pl.pb.finansista.request.repository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.RequestTemplate;
import java.util.Optional;
import java.util.UUID;
@Repository
class JpaRequestTemplateRepository implements RequestTemplateRepository {

    private final SpringDataJpaRequestTemplateRepository repository;

    public JpaRequestTemplateRepository(SpringDataJpaRequestTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RequestTemplate> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public RequestTemplate save(RequestTemplate requestTemplate) {
        return repository.save(requestTemplate);
    }
}
