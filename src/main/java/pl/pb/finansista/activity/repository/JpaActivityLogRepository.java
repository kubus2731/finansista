package pl.pb.finansista.activity.repository;
import org.springframework.stereotype.Repository;
import pl.pb.finansista.activity.ActivityLog;
import java.util.Optional;
import java.util.UUID;
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
    public ActivityLog save(ActivityLog activityLog) {
        return repository.save(activityLog);
    }
}
