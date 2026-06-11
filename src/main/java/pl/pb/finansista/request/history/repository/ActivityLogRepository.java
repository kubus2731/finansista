package pl.pb.finansista.request.history.repository;
import pl.pb.finansista.request.history.ActivityLog;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ActivityLogRepository {

    Optional<ActivityLog> findById(Long id);

    List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);

    ActivityLog save(ActivityLog activityLog);
}
