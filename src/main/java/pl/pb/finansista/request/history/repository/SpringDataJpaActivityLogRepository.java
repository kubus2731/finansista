package pl.pb.finansista.request.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.history.ActivityLog;

import java.util.List;

interface SpringDataJpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);
}
