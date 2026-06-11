package pl.pb.finansista.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.ActivityLog;

import java.util.List;

interface SpringDataJpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);
}
