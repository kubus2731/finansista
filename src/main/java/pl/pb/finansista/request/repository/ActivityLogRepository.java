package pl.pb.finansista.request.repository;

import pl.pb.finansista.request.ActivityLog;

import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository {

    Optional<ActivityLog> findById(Long id);

    List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);

    ActivityLog save(ActivityLog activityLog);
}
