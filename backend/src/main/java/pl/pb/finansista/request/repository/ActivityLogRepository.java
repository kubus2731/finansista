package pl.pb.finansista.request.repository;

import java.util.List;
import java.util.Optional;
import pl.pb.finansista.request.ActivityLog;

public interface ActivityLogRepository {

    Optional<ActivityLog> findById(Long id);

    List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);

    ActivityLog save(ActivityLog activityLog);
}
