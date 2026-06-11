package pl.pb.finansista.request.repository;

import org.springframework.stereotype.Repository;
import pl.pb.finansista.request.ActivityLog;
import java.util.Optional;
import java.util.List;

@Repository
class JpaActivityLogRepository implements ActivityLogRepository {

    private final SpringDataJpaActivityLogRepository repository;

    public JpaActivityLogRepository(SpringDataJpaActivityLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ActivityLog> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId) {
        return repository.findByRequestIdOrderByCreatedAtDesc(requestId);
    }

    @Override
    public ActivityLog save(ActivityLog activityLog) {
        return repository.save(activityLog);
    }
}
