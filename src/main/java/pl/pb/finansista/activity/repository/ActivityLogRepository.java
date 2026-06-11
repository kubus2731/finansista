package pl.pb.finansista.activity.repository;
import pl.pb.finansista.activity.ActivityLog;
import java.util.Optional;
import java.util.UUID;

public interface ActivityLogRepository {

    Optional<ActivityLog> findById(Long id);

    ActivityLog save(ActivityLog activityLog);
}
