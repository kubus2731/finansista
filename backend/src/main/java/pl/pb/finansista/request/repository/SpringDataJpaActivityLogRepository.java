package pl.pb.finansista.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pb.finansista.request.ActivityLog;

interface SpringDataJpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
  
  List<ActivityLog> findByRequestIdOrderByCreatedAtDesc(Long requestId);
}
